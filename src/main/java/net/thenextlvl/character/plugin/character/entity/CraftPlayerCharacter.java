package net.thenextlvl.character.plugin.character.entity;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.thenextlvl.character.plugin.character.PaperPlayerCharacter;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftPlayerCharacter extends CraftPlayer {
    private final PaperPlayerCharacter character;

    public CraftPlayerCharacter(PaperPlayerCharacter character, CraftServer server, ServerPlayer handle) {
        super(server, handle);
        this.character = character;
    }

    @Override
    public void setCollidable(boolean collidable) {
        if (isCollidable() == collidable) return;
        super.setCollidable(collidable);
        character.updateTeamOptions();
    }

    @Override
    public void setVisualFire(boolean fire) {
        if (isVisualFire() == fire) return;
        super.setVisualFire(fire);
        if (isTicking()) return;
        getHandle().setSharedFlagOnFire(fire);
        var update = new ClientboundSetEntityDataPacket(getEntityId(), getHandle().getEntityData().packAll());
        getTrackedBy().forEach(player -> character.sendPacket(player, update));
    }

    @Override
    public void remove() {
        getHandle().discard();
    }
}
