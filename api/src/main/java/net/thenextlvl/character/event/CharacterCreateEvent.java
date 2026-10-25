package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when a character is created.
 *
 * @since 0.9.0
 */
public final class CharacterCreateEvent extends CharacterEvent {
    private static final HandlerList handlers = new HandlerList();

    @ApiStatus.Internal
    public CharacterCreateEvent(final Character character) {
        super(character);
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
