package net.thenextlvl.character.action;

import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.character.Character;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiPredicate;

@NullMarked
public sealed interface ActionType<T> permits SimpleActionType {
    @Contract(pure = true)
    Class<T> type();

    @KeyPattern.Value
    @Contract(pure = true)
    String name();

    @Contract(pure = true)
    Action<T> action();

    @Contract(pure = true)
    boolean isApplicable(T input, Character<?> character);

    @FunctionalInterface
    interface Action<T> {
        void invoke(Player player, Entity character, T input);
    }

    /**
     * @since 0.5.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static <T> Builder<T> builder(@KeyPattern.Value String name, Class<T> type) {
        return new SimpleActionType.Builder<>(type, name);
    }

    /**
     * @since 0.5.0
     */
    sealed interface Builder<T> permits SimpleActionType.Builder {
        @Contract(mutates = "this")
        Builder<T> action(Action<T> action);

        @Contract(mutates = "this")
        Builder<T> applicable(BiPredicate<T, Character<?>> applicable);

        @Contract(value = " -> new", pure = true)
        ActionType<T> build() throws IllegalArgumentException;
    }
}
