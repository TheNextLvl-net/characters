package net.thenextlvl.character.plugin.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EmptyPacketListener extends ServerGamePacketListenerImpl {
    public EmptyPacketListener(MinecraftServer server, ServerPlayer player, CommonListenerCookie cookie) {
        super(server, new EmptyConnection(), player, cookie);
    }

    @Override
    public void resumeFlushing() {
    }

    @Override
    public void send(Packet<?> packet) {
    }
}
