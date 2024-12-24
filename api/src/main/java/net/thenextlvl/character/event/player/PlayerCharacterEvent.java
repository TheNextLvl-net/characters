package net.thenextlvl.character.event.player;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.event.CharacterEvent;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class PlayerCharacterEvent extends CharacterEvent {
    private final Player player;

    public PlayerCharacterEvent(Character<?> character, Player player) {
        super(character);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
