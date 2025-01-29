package net.thenextlvl.character.plugin.character.entity.handle;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.thenextlvl.character.plugin.character.PaperPlayerCharacter;
import net.thenextlvl.character.plugin.network.EmptyPacketListener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ServerCharacter extends ServerPlayer {
    private final PaperPlayerCharacter character;

    public ServerCharacter(PaperPlayerCharacter character, MinecraftServer server, ServerLevel level, ClientInformation information, CommonListenerCookie cookie) {
        super(server, level, character.profile.getGameProfile(), information);
        this.character = character;
        this.connection = new EmptyPacketListener(character, server, this, cookie);
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
        return character.getScoreboardName();
    }

    @Override
    public int getTeamColor() {
        return character.getTeamColor() != null ? character.getTeamColor().value() : super.getTeamColor();
    }

    @Override
    public net.minecraft.network.chat.Component getTabListDisplayName() {
        return net.minecraft.network.chat.Component.literal("[NPC] ")
                .append(character.getName())
                .withColor(getTeamColor());
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        character.updateDisplayNameHologramPosition();
    }

    @Override
    public boolean isAlwaysTicking() {
        return character.isTicking();
    }

    @Override
    public boolean isTicking() {
        return character.isTicking();
    }

    @Override
    public void tick() {
        refreshDimensions();
        if (isTicking()) super.tick();
    }

    @Override
    public void doTick() {
        if (isTicking()) super.doTick();
    }
}
