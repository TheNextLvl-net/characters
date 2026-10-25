package net.thenextlvl.character.action;

import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.character.Character;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.function.BiPredicate;

/**
 * Represents a type of action that can be performed on a character.
 *
 * @since 0.1.0
 */
public sealed interface ActionType<T> permits SimpleActionType {
    /**
     * Gets the type of input this action type requires.
     *
     * @return input type
     * @since 0.1.0
     */
    @Contract(pure = true)
    Class<T> type();

    /**
     * Gets the name of this action type.
     *
     * @return action type name
     * @since 0.1.0
     */
    @KeyPattern.Value
    @Contract(pure = true)
    String name();

    /**
     * Gets the action of this action type.
     *
     * @return action
     * @since 0.1.0
     */
    @Contract(pure = true)
    Action<T> action();

    /**
     * Checks if this action type is applicable to the given character with the given input.
     *
     * @param input     action input
     * @param character character to check
     * @return {@code true} if this action type is applicable, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isApplicable(T input, Character character);

    /**
     * Represents an action that is performed on a character.
     *
     * @since 0.1.0
     */
    @FunctionalInterface
    interface Action<T> {
        /**
         * Invokes this action on the given character for the given player.
         *
         * @param character the character the action is performed on
         * @param player    the player that performed the action
         * @param input     the input of the action
         * @since 0.9.0
         */
        void invoke(Character character, Player player, T input);
    }

    /**
     * Creates a new action type with the given name, type, and action.
     *
     * @param name   action type name
     * @param type   action type input type
     * @param action action
     * @param <T>    action type input type
     * @return new action type
     * @since 0.9.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> ActionType<T> create(@KeyPattern.Value final String name, final Class<T> type, final Action<T> action) {
        return new SimpleActionType<>(name, type, action, null);
    }

    /**
     * Creates a new action type builder with the given name and type.
     *
     * @param name action type name
     * @param type action type input type
     * @param <T>  action type input type
     * @return new action type builder
     * @since 0.5.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static <T> Builder<T> builder(@KeyPattern.Value final String name, final Class<T> type) {
        return new SimpleActionType.Builder<>(type, name);
    }

    /**
     * A builder for action types.
     *
     * @since 0.5.0
     */
    sealed interface Builder<T> permits SimpleActionType.Builder {
        /**
         * Sets the action of the action type.
         *
         * @param action action
         * @return this builder
         * @since 0.5.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> action(Action<T> action);

        /**
         * Sets the predicate that decides whether the action type is applicable to a character.
         *
         * @param applicable applicability predicate
         * @return this builder
         * @see ActionType#isApplicable(Object, Character)
         * @since 0.9.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> applicable(BiPredicate<T, Character> applicable);

        /**
         * Builds the action type.
         *
         * @return new action type
         * @throws IllegalArgumentException if no action was set
         * @since 0.5.0
         */
        @Contract(value = " -> new", pure = true)
        ActionType<T> build() throws IllegalArgumentException;
    }
}
