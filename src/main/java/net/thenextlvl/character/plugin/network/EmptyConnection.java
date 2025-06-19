package net.thenextlvl.character.plugin.network;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jspecify.annotations.NullMarked;

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
    public void setListenerForServerboundHandshake(PacketListener packetListener) {
        super.setListenerForServerboundHandshake(packetListener);
        handleDisconnection();
    }

    @Override
    public void send(Packet<?> packet) {
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
}
