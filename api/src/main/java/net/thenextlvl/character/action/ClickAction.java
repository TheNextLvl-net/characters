package net.thenextlvl.character.action;

import net.kyori.adventure.key.Keyed;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ClickAction<T> extends Keyed {
    ActionType<T> getActionType();

    ClickType[] getClickTypes();

    String getInput();

    void invoke(Player player);
}
