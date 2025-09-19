package net.thenextlvl.character.action;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.character.Character;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public sealed interface ActionType<T> permits SimpleActionType {
    @Contract(pure = true)
    Class<T> type();

    @KeyPattern.Value
    @Contract(pure = true)
    String name();

    @Contract(pure = true)
    Action<T> action();

    /**
     * @since 0.5.0
     */
    @ApiStatus.Internal
    @Contract(pure = true)
    Supplier<ArgumentBuilder<CommandSourceStack, ?>> argumentTree();

    /**
     * @since 0.5.0
     */
    @ApiStatus.Internal
    @Contract(pure = true)
    Function<CommandContext<CommandSourceStack>, T> parser();

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

        @Contract(mutates = "this")
        Builder<T> argumentTree(@Nullable Supplier<ArgumentBuilder<CommandSourceStack, ?>> argumentTree);

        @Contract(mutates = "this")
        Builder<T> parser(@Nullable Function<CommandContext<CommandSourceStack>, T> parser);

        @Contract(value = " -> new", pure = true)
        ActionType<T> build() throws IllegalArgumentException;
    }
}
