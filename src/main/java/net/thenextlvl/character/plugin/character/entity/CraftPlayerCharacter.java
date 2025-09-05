package net.thenextlvl.character.plugin.character.entity;

import net.minecraft.server.level.ServerPlayer;
import net.thenextlvl.character.plugin.character.PaperPlayerCharacter;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftPlayerCharacter extends CraftPlayer {
    private final PaperPlayerCharacter character;

    // todo: knockback & fall damage
    //  https://www.spigotmc.org/threads/npc-create-autorespawn-pathfinding-knockback-and-falldamage.649605/
    
    public CraftPlayerCharacter(PaperPlayerCharacter character, CraftServer server, ServerPlayer handle) {
        super(server, handle);
        this.character = character;
        setSleepingIgnored(true);
        setAffectsSpawning(false);
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
