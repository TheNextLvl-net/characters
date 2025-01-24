package net.thenextlvl.character.plugin.character;

import com.destroystokyo.paper.PaperSkinParts;
import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Preconditions;
import core.nbt.serialization.ParserException;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.ListTag;
import core.nbt.tag.Tag;
import core.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.network.EmptyPacketListener;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION;

@NullMarked
public class PaperPlayerCharacter extends PaperCharacter<Player> implements PlayerCharacter {
    private final CraftPlayerProfile profile;

    private SkinParts skinParts = new PaperSkinPartBuilder().build();

    private boolean listed = false;
    private boolean realPlayer = false;

    private @Nullable TextDisplay displayNameHologram;

    public PaperPlayerCharacter(CharacterPlugin plugin, String name, UUID uuid) {
        super(plugin, name, EntityType.PLAYER);
        this.profile = new CraftPlayerProfile(uuid, "NPC_" + StringUtil.random(12));
    }

    @Override
    public boolean despawn() {
        if (entity == null || !entity.isValid()) return false;
        var packet = new ClientboundRemoveEntitiesPacket(entity.getEntityId());
        plugin.getServer().getOnlinePlayers().forEach(player -> sendPacket(player, packet));
        ((CraftPlayer) entity).getHandle().discard();
        removeDisplayNameHologram();
        return true;
    }

    @Override
    public String getScoreboardName() {
        return Objects.requireNonNull(profile.getName());
    }

    @Override
    public boolean setCollidable(boolean collidable) {
        if (!super.setCollidable(collidable)) return false;
        updateTeamOptions();
        return true;
    }

    @Override
    public boolean setTeamColor(@Nullable NamedTextColor color) {
        if (!super.setTeamColor(color)) return false;
        getEntity().ifPresent(this::updateDisplayName);
        return true;
    }

    @Override
    public boolean spawn(Location location) {
        var server = (CraftServer) plugin.getServer();
        var level = ((CraftWorld) location.getWorld()).getHandle();

        if (entity != null && !entity.isValid()) {
            var handle = ((CraftPlayer) entity).getHandle();
            handle.setHealth(handle.getMaxHealth());
            if (isRealPlayer()) {
                server.getHandle().respawn(handle, false, RemovalReason.KILLED, RespawnReason.DEATH, location);
            } else {
                handle.unsetRemoved();
                handle.setServerLevel(level);
                handle.spawnIn(level);
                broadcastCharacter();
                handle.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            }
            return true;
        } else if (isSpawned()) return false;

        super.spawnLocation = location;

        var information = createClientInformation();
        var cookie = new CommonListenerCookie(profile.getGameProfile(), 0, information, false);
        var serverPlayer = new ServerCharacter(server.getServer(), level, information, cookie);

        serverPlayer.setClientLoaded(true);
        this.entity = new CraftCharacter(server, serverPlayer);

        server.getServer().getConnection().getConnections().add(serverPlayer.connection.connection);
        if (!isRealPlayer()) {
            serverPlayer.spawnReason = CreatureSpawnEvent.SpawnReason.DEFAULT;
            serverPlayer.setServerLevel(level);
            serverPlayer.spawnIn(level);
            serverPlayer.setPosRaw(location.getX(), location.getY(), location.getZ(), true);
            serverPlayer.setRot(location.getYaw(), location.getPitch());

            serverPlayer.supressTrackerForLogin = true;

            level.addNewPlayer(serverPlayer);

            serverPlayer.sentListPacket = true;
            serverPlayer.supressTrackerForLogin = false;

            level.getChunkSource().chunkMap.addEntity(serverPlayer);

            broadcastCharacter();
        } else {
            server.getHandle().placeNewPlayer(serverPlayer.connection.connection, serverPlayer, cookie);
            serverPlayer.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            entity.setSleepingIgnored(true);
        }

        preSpawn(this.entity);
        applySkinPartConfig(serverPlayer);
        startTicking(serverPlayer);
        return true;
    }

    @Override
    public void remove() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            var team = player.getScoreboard().getTeam(getScoreboardName());
            if (team != null) team.unregister();
        });
        super.remove();
    }

    @Override
    public CompoundTag serialize() throws ParserException {
        var tag = super.serialize();
        if (getGameProfile().getId() != null)
            tag.add("uuid", plugin.nbt().toTag(getGameProfile().getId()));
        var properties = new ListTag<>(CompoundTag.ID);
        getGameProfile().getProperties().forEach(property -> properties.add(plugin.nbt().toTag(property)));
        tag.add("listed", isListed());
        tag.add("properties", properties);
        tag.add("realPlayer", isRealPlayer());
        tag.add("skinParts", (byte) getSkinParts().getRaw());
        return tag;
    }

    @Override
    public void deserialize(Tag tag) throws ParserException {
        var root = tag.getAsCompound();
        root.optional("listed").map(Tag::getAsBoolean).ifPresent(this::setListed);
        root.optional("properties").map(Tag::getAsList).map(tags -> tags.stream()
                .map(t -> plugin.nbt().fromTag(t, ProfileProperty.class))
                .toList()
        ).ifPresent(getGameProfile()::setProperties);
        root.optional("realPlayer").map(Tag::getAsBoolean).ifPresent(this::setRealPlayer);
        root.optional("skinParts").map(Tag::getAsByte).map(PaperSkinParts::new).ifPresent(this::setSkinParts);
        super.deserialize(tag);
    }

    @Override
    protected void updateDisplayName(Player player) {
        updateDisplayNameHologram(player);
        updateTeamOptions();
    }

    @Override
    public PlayerProfile getGameProfile() {
        return profile;
    }

    @Override
    public @Nullable ProfileProperty getTextures() {
        return getGameProfile().getProperties().stream()
                .filter(property -> property.getName().equals("textures"))
                .findAny().orElse(null);
    }

    @Override
    public SkinParts getSkinParts() {
        return skinParts;
    }

    @Override
    public boolean setSkinParts(SkinParts parts) {
        if (this.skinParts == parts) return false;
        this.skinParts = parts;
        getEntity(CraftPlayer.class).map(CraftPlayer::getHandle)
                .ifPresent(this::applySkinPartConfig);
        return true;
    }

    @Override
    public UUID getUniqueId() {
        return Objects.requireNonNull(profile.getId());
    }

    @Override
    public boolean clearTextures() {
        if (!getGameProfile().getProperties().removeIf(property ->
                property.getName().equals("textures"))) return false;
        update();
        return true;
    }

    @Override
    public boolean isListed() {
        return listed;
    }

    @Override
    public boolean setListed(boolean listed) {
        if (this.listed == listed) return false;
        this.listed = listed;
        getEntity(CraftPlayer.class).ifPresent(entity -> {
            entity.getHandle().updateOptionsNoEvents(createClientInformation());
            var update = ClientboundPlayerInfoUpdatePacket.updateListed(entity.getUniqueId(), listed);
            entity.getTrackedBy().forEach(player -> sendPacket(player, update));
        });
        return true;
    }

    @Override
    public boolean isRealPlayer() {
        return realPlayer;
    }

    @Override
    public boolean setRealPlayer(boolean real) {
        if (this.realPlayer == real) return false;
        this.realPlayer = real;
        return true;
    }

    @Override
    public boolean setTextures(String value, @Nullable String signature) {
        var previous = getTextures();
        if (previous != null && previous.getValue().equals(value)
            && Objects.equals(previous.getSignature(), signature)) return false;
        getGameProfile().getProperties().removeIf(property -> property.getName().equals("textures"));
        getGameProfile().getProperties().add(new ProfileProperty("textures", value, signature));
        update();
        return true;
    }

    public void loadCharacter(Player player) {
        getEntity().ifPresent(entity -> {
            var handle = ((CraftPlayer) entity).getHandle();
            if (isVisibleByDefault()) sendPacket(player, new ClientboundBundlePacket(List.of(
                    createAddPacket(handle), createInitializationPacket(handle))));
            else if (canSee(player)) player.showEntity(plugin, entity);
            else player.hideEntity(plugin, entity);
            updateTeamOptions(getCharacterSettingsTeam(player));
        });
    }

    private void applySkinPartConfig(ServerPlayer player) {
        player.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) getSkinParts().getRaw());
    }

    private void broadcastCharacter() {
        getEntity().ifPresent(entity -> {
            var handle = ((CraftPlayer) entity).getHandle();
            var packets = new ClientboundBundlePacket(List.of(
                    createInitializationPacket(handle), createAddPacket(handle)
            ));
            entity.getWorld().getPlayers().forEach(player -> sendPacket(player, packets));
        });
    }

    private ClientboundAddEntityPacket createAddPacket(ServerPlayer entity) {
        return new ClientboundAddEntityPacket(entity.getId(), entity.getUUID(),
                entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot(),
                entity.getType(), 0, entity.getDeltaMovement(), entity.getYHeadRot());
    }

    private @NotNull ClientInformation createClientInformation() {
        return new ClientInformation("en_us", 2, ChatVisiblity.HIDDEN, true, skinParts.getRaw(), HumanoidArm.RIGHT, false, isListed(), ParticleStatus.MINIMAL);
    }

    private ClientboundPlayerInfoUpdatePacket createInitializationPacket(ServerPlayer entity) {
        return ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(entity, isListed());
    }

    private Team getCharacterSettingsTeam(Player player) {
        var characterSettings = player.getScoreboard().getTeam(getScoreboardName());
        if (characterSettings != null) return characterSettings;
        characterSettings = player.getScoreboard().registerNewTeam(getScoreboardName());
        characterSettings.addEntry(getScoreboardName());
        return characterSettings;
    }

    private Location getDisplayNameHologramPosition(Player player) {
        var location = player.getLocation().clone();
        var incrementor = switch (getPose()) {
            case SNEAKING -> 0.15;
            default -> 0.27;
        };
        location.setY(player.getBoundingBox().getMaxY() + incrementor);
        return location;
    }

    private void removeDisplayNameHologram() {
        if (displayNameHologram == null) return;
        displayNameHologram.remove();
        displayNameHologram = null;
    }

    private void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    private void spawnDisplayNameHologram(Player player) {
        Preconditions.checkState(displayNameHologram == null, "DisplayNameHologram already spawned");
        var location = getDisplayNameHologramPosition(player);
        displayNameHologram = player.getWorld().spawn(location, TextDisplay.class, this::updateDisplayNameHologram);
    }

    @Override
    public boolean setTicking(boolean ticking) {
        if (!super.setTicking(ticking)) return false;
        getEntity(CraftPlayer.class).map(CraftPlayer::getHandle)
                .ifPresent(this::startTicking);
        return true;
    }

    private void startTicking(ServerPlayer serverPlayer) {
        if (ticking) plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> {
            if (!ticking || entity == null) scheduledTask.cancel();
            else if (serverPlayer.valid) serverPlayer.doTick();
        }, 1, 1);
    }

    private boolean update() {
        if (entity == null) return false;
        var handle = ((CraftPlayer) entity).getHandle();
        var remove = new ClientboundRemoveEntitiesPacket(handle.getId());
        var removeInfo = new ClientboundPlayerInfoRemovePacket(List.of(handle.getUUID()));
        var update = new ClientboundSetEntityDataPacket(handle.getId(), handle.getEntityData().packAll());
        var packets = new ClientboundBundlePacket(List.of(
                remove, removeInfo, createInitializationPacket(handle), createAddPacket(handle), update
        ));
        entity.getTrackedBy().forEach(player -> sendPacket(player, packets));
        return true;
    }

    private void updateDisplayNameHologram(TextDisplay display) {
        display.setAlignment(tagOptions.getAlignment());
        display.setBackgroundColor(tagOptions.getBackgroundColor() != null
                ? tagOptions.getBackgroundColor() : Color.fromARGB(1073741824));
        display.setBillboard(tagOptions.getBillboard());
        display.setBrightness(tagOptions.getBrightness());
        display.setDefaultBackground(tagOptions.isDefaultBackground());
        display.setLineWidth(tagOptions.getLineWidth());
        display.setGravity(false);
        display.setPersistent(false);
        display.setSeeThrough(tagOptions.isSeeThrough());
        display.setShadowed(tagOptions.hasTextShadow());
        display.setTeleportDuration(3);
        display.setTextOpacity((byte) Math.round(25f + ((100f - tagOptions.getTextOpacity()) * 2.3f)));
        display.setTransformation(new Transformation(
                display.getTransformation().getTranslation(),
                display.getTransformation().getLeftRotation(),
                tagOptions.getScale(),
                display.getTransformation().getRightRotation()
        ));
        var component = displayName == null ? Component.text(getName()) : displayName;
        display.text(component.colorIfAbsent(teamColor));
    }

    private void updateDisplayNameHologram(Player player) {
        if (!displayNameVisible) {
            removeDisplayNameHologram();
        } else if (displayNameHologram == null) {
            spawnDisplayNameHologram(player);
        } else {
            updateDisplayNameHologram(displayNameHologram);
        }
    }

    private void updateTeamOptions() {
        plugin.getServer().getOnlinePlayers().forEach(player ->
                updateTeamOptions(getCharacterSettingsTeam(player)));
    }

    private void updateTeamOptions(Team team) {
        team.color(teamColor);
        team.setOption(Option.COLLISION_RULE, collidable ? OptionStatus.ALWAYS : OptionStatus.NEVER);
        team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
    }

    private class CraftCharacter extends CraftPlayer {
        public CraftCharacter(CraftServer server, ServerPlayer handle) {
            super(server, handle);
        }

        @Override
        public void remove() {
            PaperPlayerCharacter.this.remove();
        }
    }

    private class ServerCharacter extends ServerPlayer {
        public ServerCharacter(MinecraftServer server, ServerLevel level, ClientInformation information, CommonListenerCookie cookie) {
            super(server, level, PaperPlayerCharacter.this.profile.getGameProfile(), information);
            this.connection = new EmptyPacketListener(PaperPlayerCharacter.this, server, this, cookie);
        }

        @Override
        public boolean isPushable() {
            return false;
        }

        @Override
        public boolean isCollidable(boolean ignoreClimbing) {
            return false;
        }

        @Override
        public boolean isPushedByFluid() {
            return true;
        }

        @Override
        public String getScoreboardName() {
            return PaperPlayerCharacter.this.getScoreboardName();
        }

        @Override
        public void setPos(double x, double y, double z) {
            super.setPos(x, y, z);
            updateDisplayNameHologramPosition();
        }

        @Override
        public void tick() {
            refreshDimensions();
            if (ticking) super.tick();
        }

        @Override
        public void doTick() {
            if (ticking) super.doTick();
        }

        private void updateDisplayNameHologramPosition() {
            if (displayNameHologram == null || entity == null) return;
            displayNameHologram.teleport(getDisplayNameHologramPosition(entity));
        }
    }
}
