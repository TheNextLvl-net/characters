package net.thenextlvl.character.plugin.network;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.SocketAddress;

@NullMarked
class EmptyConnection extends Connection {
    public EmptyConnection() {
        super(PacketFlow.CLIENTBOUND);
        this.channel = new EmptyChannel();
        this.address = new SocketAddress() {
        };
    }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> protocolInfo, @NonNull T packetInfo) {
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
    public void send(Packet<?> packet) {
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener sendListener) {
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener listener, boolean flush) {
    }

    @Override
    public void setListenerForServerboundHandshake(PacketListener packetListener) {
        super.setListenerForServerboundHandshake(packetListener);
        handleDisconnection();
    }
}