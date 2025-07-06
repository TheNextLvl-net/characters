package net.thenextlvl.character.plugin.character.action;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ActionType;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiPredicate;

@NullMarked
public record PaperActionType<T>(
        String name, Class<T> type, Action<T> action,
        BiPredicate<T, Character<?>> applicable
) implements ActionType<T> {

    public PaperActionType(String name, Class<T> type, Action<T> action) {
        this(name, type, action, (input, character) -> true);
    }

    @Override
    public boolean isApplicable(T input, Character<?> character) {
        return applicable.test(input, character);
    }
}
