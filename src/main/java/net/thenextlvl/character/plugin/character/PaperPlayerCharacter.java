package net.thenextlvl.character.plugin.character;

import com.destroystokyo.paper.PaperSkinParts;
import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import core.nbt.serialization.ParserException;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.ListTag;
import core.nbt.tag.Tag;
import core.util.StringUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
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
import net.thenextlvl.character.plugin.character.entity.CraftPlayerCharacter;
import net.thenextlvl.character.plugin.network.EmptyPacketListener;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
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

    public PaperPlayerCharacter(CharacterPlugin plugin, String name, UUID uuid) {
        super(plugin, name, EntityType.PLAYER);
        this.profile = new CraftPlayerProfile(uuid, "NPC_" + StringUtil.random(12));
    }

    @Override
    public String getScoreboardName() {
        return Objects.requireNonNull(profile.getName());
    }

    @Override
    public boolean setTeamColor(@Nullable NamedTextColor color) {
        if (!super.setTeamColor(color)) return false;
        getEntity(CraftPlayer.class).ifPresent(entity -> {
            var update = new ClientboundPlayerInfoUpdatePacket(Action.UPDATE_DISPLAY_NAME, entity.getHandle());
            entity.getTrackedBy().forEach(player -> sendPacket(player, update));
        });
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
        this.entity = new CraftPlayerCharacter(this, server, serverPlayer);

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
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> {
            if (entity == null) scheduledTask.cancel();
            else if (serverPlayer.valid) serverPlayer.doTick();
        }, 1, 1);
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
        getEntity(CraftPlayer.class).ifPresent(entity -> {
            if (isVisibleByDefault()) sendPacket(player, new ClientboundBundlePacket(List.of(
                    createAddPacket(entity.getHandle()), createInitializationPacket(entity.getHandle()))));
            else if (canSee(player)) player.showEntity(plugin, entity);
            else player.hideEntity(plugin, entity);
            updateTeamOptions(getCharacterSettingsTeam(player));
        });
    }

    private void applySkinPartConfig(ServerPlayer player) {
        player.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) getSkinParts().getRaw());
    }

    private void broadcastCharacter() {
        getEntity(CraftPlayer.class).ifPresent(entity -> {
            var handle = entity.getHandle();
            var packets = new ClientboundBundlePacket(List.of(
                    createInitializationPacket(entity.getHandle()),
                    createAddPacket(entity.getHandle())
            ));
            entity.getWorld().getPlayersSeeingChunk(entity.getChunk()).stream()
                    .filter(this::canSee)
                    .forEach(player -> sendPacket(player, packets));
        });
    }

    private ClientboundAddEntityPacket createAddPacket(ServerPlayer entity) {
        return new ClientboundAddEntityPacket(entity.getId(), entity.getUUID(),
                entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot(),
                entity.getType(), 0, entity.getDeltaMovement(), entity.getYHeadRot());
    }

    private ClientInformation createClientInformation() {
        return new ClientInformation("en_us", 2, ChatVisiblity.HIDDEN, true, skinParts.getRaw(), HumanoidArm.RIGHT, false, isListed(), ParticleStatus.MINIMAL);
    }

    private ClientboundPlayerInfoUpdatePacket createInitializationPacket(ServerPlayer entity) {
        return ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(entity, isListed());
    }

    public void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
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

    @Override
    protected boolean showDisplayName() {
        return displayNameVisible;
    }

    public class ServerCharacter extends ServerPlayer {
        public ServerCharacter(MinecraftServer server, ServerLevel level, ClientInformation information, CommonListenerCookie cookie) {
            super(server, level, profile.getGameProfile(), information);
            this.connection = new EmptyPacketListener(server, this, cookie);
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
        public int getTeamColor() {
            return teamColor != null ? teamColor.value() : super.getTeamColor();
        }

        @Override
        public net.minecraft.network.chat.Component getTabListDisplayName() {
            return net.minecraft.network.chat.Component.literal("[NPC] ")
                    .append(PaperPlayerCharacter.this.getName())
                    .withColor(getTeamColor());
        }
    }
}
