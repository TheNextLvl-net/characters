package net.thenextlvl.character.event;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.Character;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a character is teleported.
 *
 * @since 0.9.0
 */
public final class CharacterTeleportEvent extends CharacterEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Location from;
    private Location to;
    private boolean cancelled;

    @ApiStatus.Internal
    public CharacterTeleportEvent(final Character character, final Location from, final Location to) {
        super(character);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the location the character is teleporting from.
     *
     * @return the origin location
     * @since 0.9.0
     */
    @Contract(value = " -> new", pure = true)
    public Location getFrom() {
        return from.clone();
    }

    /**
     * Returns the location the character is teleporting to.
     *
     * @return the destination location
     * @since 0.9.0
     */
    @Contract(value = " -> new", pure = true)
    public Location getTo() {
        return to.clone();
    }

    /**
     * Sets the location the character will teleport to.
     *
     * @param to the destination location
     * @throws IllegalArgumentException if the world of the location is null
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    public void setTo(final Location to) {
        Preconditions.checkArgument(to.getWorld() != null, "World cannot be null");
        this.to = to.clone();
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
