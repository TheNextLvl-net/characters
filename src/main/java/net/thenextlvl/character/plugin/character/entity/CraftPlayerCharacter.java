package net.thenextlvl.character.plugin.character.entity;

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
        setSleepingIgnored(true);
    }

    @Override
    public void setCollidable(boolean collidable) {
        if (isCollidable() == collidable) return;
        super.setCollidable(collidable);
        character.getEntity().ifPresent(character::updateTeamOptions);
    }

    @Override
    public void remove() {
        getHandle().discard();
    }
}
