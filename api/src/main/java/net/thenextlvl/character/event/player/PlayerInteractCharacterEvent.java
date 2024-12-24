package net.thenextlvl.character.event.player;

import net.thenextlvl.character.Character;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerInteractCharacterEvent extends PlayerCharacterEvent implements Cancellable {
    private boolean cancelled;

    public PlayerInteractCharacterEvent(Character<?> character, Player player) {
        super(character, player);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
