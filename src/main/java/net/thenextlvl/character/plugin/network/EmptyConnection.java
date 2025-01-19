package net.thenextlvl.character.plugin.network;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.PaperPlayerCharacter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.SocketAddress;

@NullMarked
class EmptyConnection extends Connection {
    private final PaperPlayerCharacter character;

    public EmptyConnection(PaperPlayerCharacter character) {
        super(PacketFlow.CLIENTBOUND);
        this.character = character;
        this.channel = new EmptyChannel();
        this.address = new SocketAddress() {
        };
    }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(@Nullable ProtocolInfo<T> protocolInfo, @NonNull T packetInfo) {
        try {
            var packetListener = Connection.class.getDeclaredField("packetListener");
            packetListener.trySetAccessible();
            packetListener.set(this, packetInfo);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            var plugin = JavaPlugin.getPlugin(CharacterPlugin.class);
            plugin.getComponentLogger().error("Failed to set field packetListener", e);
        }
    }

    @Override
    public void setListenerForServerboundHandshake(PacketListener packetListener) {
        super.setListenerForServerboundHandshake(packetListener);
        handleDisconnection();
    }

    @Override
    public void send(Packet<?> packet) {
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener sendListener) {
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener listener, boolean flush) {
    }

    @Override
    public void flushChannel() {
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isConnecting() {
        return false;
    }

    @Override
    public void tick() {
        if (character.isTicking()) super.tick();
    }
}
