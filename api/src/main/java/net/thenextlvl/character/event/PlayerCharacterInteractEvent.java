package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a player interacts with a character.
 * <p>
 * Canceling this event will prevent all click actions from being invoked.
 *
 * @since 0.9.0
 */
public final class PlayerCharacterInteractEvent extends CharacterEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ClickType clickType;
    private boolean cancelled;

    @ApiStatus.Internal
    public PlayerCharacterInteractEvent(final Character character, final Player player, final ClickType clickType) {
        super(character);
        this.player = player;
        this.clickType = clickType;
    }

    /**
     * Returns the player who interacted with the character.
     *
     * @return the player
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the type of click performed.
     *
     * @return the click type
     * @since 0.9.0
     */
    @Contract(pure = true)
    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
