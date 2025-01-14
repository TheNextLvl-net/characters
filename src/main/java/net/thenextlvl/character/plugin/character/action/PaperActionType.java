package net.thenextlvl.character.plugin.character.action;

import net.thenextlvl.character.action.ActionType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

@NullMarked
public record PaperActionType<T>(String name, Class<T> type, BiConsumer<Player, T> function) implements ActionType<T> {
    @Override
    public void invoke(Player player, T input) {
        function.accept(player, input);
    }
}
