package net.thenextlvl.character.action;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ActionType<T> {
    String getName();

    void invoke(Player player, T input);
}
