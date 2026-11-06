package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when the entity of a character dies.
 * <p>
 * Every viewer has their own entity of the character, it respawns for the viewer afterwards.
 *
 * @see CharacterRespawnEvent
 * @since 0.9.0
 */
public final class CharacterDeathEvent extends CharacterEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Player viewer;

    @ApiStatus.Internal
    public CharacterDeathEvent(final Character character, final Player viewer) {
        super(character);
        this.viewer = viewer;
    }

    /**
     * Returns the viewer of the entity that died.
     *
     * @return the viewer
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Player getViewer() {
        return viewer;
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
