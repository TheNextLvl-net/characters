package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CharacterEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Character<?> character;

    @ApiStatus.Internal
    protected CharacterEvent(Character<?> character) {
        this.character = character;
    }

    @Contract(pure = true)
    public Character<?> getCharacter() {
        return character;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
