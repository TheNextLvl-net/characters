package net.thenextlvl.character.model;

import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.thenextlvl.character.CharacterPlugin;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.model.packet.EmptyPacketListener;
import net.thenextlvl.character.skin.Skin;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.profile.PlayerTextures;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION;

@NullMarked
public class PaperPlayerCharacter extends PaperCharacter<Player> implements PlayerCharacter {
    private CraftPlayerProfile profile;
    private boolean listed;
    private boolean realPlayer = false;
    private final Skin skin = new CharacterSkin();
    private final UUID uuid;

    public PaperPlayerCharacter(CharacterPlugin plugin, String name) {
        this(plugin, name, UUID.randomUUID());
    }

    public PaperPlayerCharacter(CharacterPlugin plugin, String name, UUID uuid) {
        super(plugin, name, EntityType.PLAYER);
        this.profile = new CraftPlayerProfile(uuid, name);
        this.uuid = uuid;
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
                broadcastPlayerAdd(level, handle);
                handle.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            }
            return true;
        } else if (isSpawned()) return false;

        super.spawnLocation = location;

        // todo: fix skin not applying
        var gameProfile = new GameProfile(uuid, name);
        this.profile.getProperties().forEach(property -> {
            var value = new Property(property.getName(), property.getValue(), property.getSignature());
            gameProfile.getProperties().put(property.getName(), value);
        });
        this.profile = new CraftPlayerProfile(gameProfile);

        var information = ClientInformation.createDefault();
        var serverPlayer = new ServerPlayer(server.getServer(), level, gameProfile, information);

        var cookie = new CommonListenerCookie(gameProfile, 0, information, false);
        serverPlayer.connection = new EmptyPacketListener(server.getServer(), serverPlayer, cookie);
        serverPlayer.setClientLoaded(true);
        this.entity = new CraftCharacter(server, serverPlayer);

        server.getServer().getConnection().getConnections().add(serverPlayer.connection.connection);
        if (!isRealPlayer()) {
            serverPlayer.moonrise$setRealPlayer(false);

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

            broadcastPlayerInfo();
            broadcastPlayerAdd(level, serverPlayer);
        } else {
            server.getHandle().placeNewPlayer(serverPlayer.connection.connection, serverPlayer, cookie);
            serverPlayer.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            entity.setSleepingIgnored(true);
        }

        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> {
            if (entity.isValid()) serverPlayer.doTick();
            else if (entity == null) scheduledTask.cancel();
        }, 1, 1);

        preSpawn(this.entity);
        applySkinPartConfig(serverPlayer);
        updatePlayerStatus(level, serverPlayer, false);
        return true;
    }

    private void broadcastPlayerAdd(ServerLevel level, ServerPlayer handle) {
        var packet = new ClientboundAddEntityPacket(handle.getId(), handle.getUUID(),
                handle.getX(), handle.getY(), handle.getZ(), handle.getXRot(), handle.getYRot(),
                handle.getType(), 0, handle.getDeltaMovement(), handle.getYHeadRot());
        plugin.getServer().getOnlinePlayers().forEach(player -> sendPacket(player, packet));
    }

    public void broadcastPlayerInfo() {
        getEntity().ifPresent(entity -> {
            var handle = ((CraftPlayer) entity).getHandle();
            var packet = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(handle, isListed());
            entity.getWorld().getPlayers().forEach(player -> sendPacket(player, packet));
        });
    }

    public void sendPlayer(Player player) {
        getEntity().ifPresent(entity -> {
            var handle = ((CraftPlayer) entity).getHandle();
            var packet = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(handle, isListed());
            sendPacket(player, packet);
        });
    }

    private void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    private void updatePlayerStatus(ServerLevel level, ServerPlayer serverPlayer, boolean remove) {
        var players = level.players();
        if (remove && players.contains(serverPlayer)) {
            players.remove(serverPlayer);
        } else if (!remove && !players.contains(serverPlayer)) {
            players.add(serverPlayer);
        } else return;

        try {
            var method = ChunkMap.class.getDeclaredMethod("updatePlayerStatus", ServerPlayer.class, boolean.class);
            method.setAccessible(true);
            method.invoke(level.getChunkSource().chunkMap, serverPlayer, !remove);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            plugin.getComponentLogger().error("Failed to update player status", e);
        }
    }

    @Override
    public boolean despawn() {
        if (entity == null || !entity.isValid()) return false;
        plugin.getServer().getOnlinePlayers().forEach(player -> sendPacket(player,
                new ClientboundRemoveEntitiesPacket(entity.getEntityId())));
        entity.setHealth(0);
        // todo: fix entity still being at the previous position
        return true;
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

    @Override
    public boolean isListed() {
        return listed;
    }

    @Override
    public boolean isRealPlayer() {
        return realPlayer;
    }

    @Override
    public void setListed(boolean listed) {
        this.listed = listed;
    }

    @Override
    public void setRealPlayer(boolean real) {
        this.realPlayer = real;
    }

    private void applySkinPartConfig(ServerPlayer player) {
        player.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) getSkin().getParts().getRaw());
    }

    @NullMarked
    public class CharacterSkin implements Skin {
        private SkinParts parts = new CharacterSkinPartBuilder().build();

        @Override
        public PlayerTextures getTextures() {
            return profile.getTextures();
        }

        @Override
        public SkinParts getParts() {
            return parts;
        }

        @Override
        public void setParts(SkinParts parts) {
            this.parts = parts;
            getEntity().map(player -> ((CraftPlayer) player).getHandle())
                    .ifPresent(PaperPlayerCharacter.this::applySkinPartConfig);
        }

        @Override
        public void setTextures(PlayerTextures textures) {
            profile.setTextures(textures);
        }
    }

    private class CraftCharacter extends CraftPlayer {
        public CraftCharacter(CraftServer server, ServerPlayer handle) {
            super(server, handle);
        }

        @Override
        public void remove() {
            despawn();
        }
    }
}
