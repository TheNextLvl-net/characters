package net.thenextlvl.character.event.player;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerCharacterClickEvent extends PlayerCharacterEvent implements Cancellable {
    private final ClickType type;
    private boolean cancelled;

    public PlayerCharacterClickEvent(Character<?> character, Player player, ClickType type) {
        super(character, player);
        this.type = type;
    }

    /**
     * Retrieves the type of click performed during the event.
     *
     * @return the {@code ClickType} representing the type of click
     */
    public ClickType getType() {
        return type;
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
