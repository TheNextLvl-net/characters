package net.thenextlvl.character.plugin.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.thenextlvl.character.plugin.character.PaperPlayerCharacter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EmptyPacketListener extends ServerGamePacketListenerImpl {
    private final PaperPlayerCharacter character;

    public EmptyPacketListener(PaperPlayerCharacter character, MinecraftServer server, ServerPlayer player, CommonListenerCookie cookie) {
        super(server, new EmptyConnection(character), player, cookie);
        this.character = character;
        if (!(connection instanceof EmptyConnection emptyConnection)) return;
        emptyConnection.setupInboundProtocol(null, this);
    }

    @Override
    public void resumeFlushing() {
    }

    @Override
    public void send(Packet<?> packet) {
    }

    @Override
    public void tick() {
        if (character.isTicking()) super.tick();
    }
}
