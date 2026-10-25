package net.thenextlvl.character.action;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * Factory for creating click actions.
 *
 * @since 0.9.0
 */
@ApiStatus.NonExtendable
public interface ClickActionFactory {
    /**
     * Creates a new click action with the given action type, click types, and input.
     *
     * @param actionType action type
     * @param clickTypes click types
     * @param input      input
     * @param <T>        input type
     * @return new click action
     * @since 0.9.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    <T> ClickAction<T> create(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input);

    /**
     * Creates a new click action with the given action type, click types, input, and configurator.
     *
     * @param actionType   action type
     * @param clickTypes   click types
     * @param input        input
     * @param configurator configurator
     * @param <T>          input type
     * @return new click action
     * @since 0.9.0
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    <T> ClickAction<T> create(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input, final Consumer<ClickAction<T>> configurator);
}
