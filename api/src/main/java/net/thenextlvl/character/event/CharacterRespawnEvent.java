package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when the entity of a character respawns for a viewer after it died.
 * <p>
 * Cancelling the event keeps the character despawned for the viewer,
 * until it is spawned again, for example when the chunk of the character is sent to the viewer again.
 *
 * @see CharacterDeathEvent
 * @since 0.9.0
 */
public final class CharacterRespawnEvent extends CharacterEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player viewer;
    private boolean cancelled;

    @ApiStatus.Internal
    public CharacterRespawnEvent(final Character character, final Player viewer) {
        super(character);
        this.viewer = viewer;
    }

    /**
     * Returns the viewer the entity respawns for.
     *
     * @return the viewer
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Player getViewer() {
        return viewer;
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
