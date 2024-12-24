package net.thenextlvl.character.event;

import net.thenextlvl.character.Character;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CharacterEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Character<?> character;

    public CharacterEvent(Character<?> character) {
        this.character = character;
    }

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
