package net.thenextlvl.character.action;

import net.thenextlvl.character.Character;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ActionType<T> {
    Class<T> type();

    String name();

    Action<T> action();
    
    boolean isApplicable(T input, Character<?> character);

    @FunctionalInterface
    interface Action<T> {
        void invoke(Player player, Entity character, T input);
    }
}
