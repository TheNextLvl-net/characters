package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

/**
 * Called when a viewer is removed from a character.
 *
 * @since 0.9.0
 */
public final class CharacterViewerRemoveEvent extends CharacterEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final UUID viewer;
    private boolean cancelled;

    @ApiStatus.Internal
    public CharacterViewerRemoveEvent(final Character character, final UUID viewer) {
        super(character);
        this.viewer = viewer;
    }

    /**
     * Returns the viewer being removed.
     *
     * @return the viewer UUID
     * @since 0.9.0
     */
    @Contract(pure = true)
    public UUID getViewer() {
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
