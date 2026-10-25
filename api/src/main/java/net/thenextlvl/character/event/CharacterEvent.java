package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Called when an event related to a character occurs.
 *
 * @since 0.9.0
 */
abstract class CharacterEvent extends Event {
    private final Character character;

    @ApiStatus.Internal
    protected CharacterEvent(final Character character) {
        super(!Bukkit.isPrimaryThread());
        this.character = character;
    }

    /**
     * Returns the character.
     *
     * @return the character
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Character getCharacter() {
        return character;
    }
}
