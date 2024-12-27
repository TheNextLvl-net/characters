package net.thenextlvl.character.plugin.model;

import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
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
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.CharacterPlugin;
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

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION;

@NullMarked
public class PaperPlayerCharacter extends PaperCharacter<Player> implements PlayerCharacter {
    private boolean listed;
    private boolean realPlayer = false;
    private final CraftPlayerProfile profile;
    private SkinParts skinParts = new PaperSkinPartBuilder().build();

    public PaperPlayerCharacter(CharacterPlugin plugin, String name, UUID uuid) {
        super(plugin, name, EntityType.PLAYER);
        this.profile = new CraftPlayerProfile(uuid, name);
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
                broadcastPlayer();
                handle.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            }
            return true;
        } else if (isSpawned()) return false;

        super.spawnLocation = location;

        var information = ClientInformation.createDefault();
        var serverPlayer = new ServerPlayer(server.getServer(), level, profile.getGameProfile(), information);

        var cookie = new CommonListenerCookie(profile.getGameProfile(), 0, information, false);
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

            broadcastPlayer();
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

    public void broadcastPlayer() {
        getEntity().ifPresent(entity -> {
            var handle = ((CraftPlayer) entity).getHandle();
            var add = new ClientboundAddEntityPacket(handle.getId(), handle.getUUID(),
                    handle.getX(), handle.getY(), handle.getZ(), handle.getXRot(), handle.getYRot(),
                    handle.getType(), 0, handle.getDeltaMovement(), handle.getYHeadRot());
            var initializing = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(handle, isListed());
            entity.getWorld().getPlayers().forEach(player -> {
                sendPacket(player, initializing);
                sendPacket(player, add);
            });
        });
    }

    public void sendPlayer(Player player) {
        getEntity().map(entity -> ((CraftPlayer) entity).getHandle())
                .ifPresent(entity -> sendPlayer(entity, player));
    }

    private void sendPlayer(ServerPlayer entity, Player player) {
        var add = new ClientboundAddEntityPacket(entity.getId(), entity.getUUID(),
                entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot(),
                entity.getType(), 0, entity.getDeltaMovement(), entity.getYHeadRot());
        var initializing = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(entity, isListed());
        sendPacket(player, initializing);
        sendPacket(player, add);
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
    public PlayerProfile getGameProfile() {
        return profile;
    }

    @Override
    public SkinParts getSkinParts() {
        return skinParts;
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

    @Override
    public void setSkinParts(SkinParts parts) {
        this.skinParts = parts;
        getEntity().map(player -> ((CraftPlayer) player).getHandle())
                .ifPresent(this::applySkinPartConfig);
    }

    private void applySkinPartConfig(ServerPlayer player) {
        player.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) getSkinParts().getRaw());
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
