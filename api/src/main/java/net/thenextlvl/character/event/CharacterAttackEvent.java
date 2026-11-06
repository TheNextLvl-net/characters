package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a character attacks a player.
 * <p>
 * Every viewer has their own entity of the character, a player is only ever attacked by their own.
 * Cancelling the event prevents the damage, the entity still swings.
 *
 * @see net.thenextlvl.character.goal.AttackGoal
 * @since 0.9.0
 */
public final class CharacterAttackEvent extends CharacterEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player target;
    private boolean cancelled;

    @ApiStatus.Internal
    public CharacterAttackEvent(final Character character, final Player target) {
        super(character);
        this.target = target;
    }

    /**
     * Returns the attacked player, who is also the viewer of the attacking entity.
     *
     * @return the attacked player
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Player getTarget() {
        return target;
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
