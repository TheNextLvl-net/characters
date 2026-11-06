package net.thenextlvl.character.event;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.Character;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when the entity of a character is damaged, only if the character is not invulnerable.
 * <p>
 * Every viewer has their own entity of the character with its own health.
 *
 * @since 0.9.0
 */
public final class CharacterDamageEvent extends CharacterEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player viewer;
    private final DamageSource source;
    private double damage;
    private boolean cancelled;

    @ApiStatus.Internal
    public CharacterDamageEvent(final Character character, final Player viewer, final DamageSource source, final double damage) {
        super(character);
        this.viewer = viewer;
        this.source = source;
        this.damage = damage;
    }

    /**
     * Returns the viewer of the damaged entity.
     *
     * @return the viewer
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Player getViewer() {
        return viewer;
    }

    /**
     * Returns the source of the damage.
     *
     * @return the damage source
     * @since 0.9.0
     */
    @Contract(pure = true)
    public DamageSource getDamageSource() {
        return source;
    }

    /**
     * Returns the damage.
     *
     * @return the damage
     * @since 0.9.0
     */
    @Contract(pure = true)
    public double getDamage() {
        return damage;
    }

    /**
     * Sets the damage.
     *
     * @param damage the damage
     * @throws IllegalArgumentException if the damage is negative
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    public void setDamage(final double damage) throws IllegalArgumentException {
        Preconditions.checkArgument(damage >= 0, "Damage cannot be negative");
        this.damage = damage;
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
