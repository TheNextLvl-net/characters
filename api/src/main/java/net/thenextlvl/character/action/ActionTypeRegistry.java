package net.thenextlvl.character.action;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/**
 * Represents a registry for action types.
 *
 * @since 0.5.0
 */
public sealed interface ActionTypeRegistry permits SimpleActionTypeRegistry {
    /**
     * Gets the action type registry instance.
     *
     * @return action type registry instance
     * @since 0.5.0
     */
    @Contract(pure = true)
    static ActionTypeRegistry registry() {
        return SimpleActionTypeRegistry.INSTANCE;
    }

    /**
     * Registers the given action type.
     *
     * @param type action type to register
     * @return {@code true} if the action type was successfully registered, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    boolean register(ActionType<?> type);

    /**
     * Checks if the given action type is registered.
     *
     * @param type action type to check
     * @return {@code true} if the action type is registered, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(pure = true)
    boolean isRegistered(ActionType<?> type);

    /**
     * Checks if an action type with the given name is registered.
     *
     * @param name action type name to check
     * @return {@code true} if an action type with the given name is registered, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(pure = true)
    boolean isRegistered(String name);

    /**
     * Unregisters the given action type.
     *
     * @param type action type to unregister
     * @return {@code true} if the action type was successfully unregistered, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    boolean unregister(ActionType<?> type);

    /**
     * Gets an action type with the given name.
     *
     * @param name action type name
     * @param <T>  action type input type
     * @return action type with the given name
     * @since 0.5.0
     */
    @Contract(pure = true)
    <T> Optional<ActionType<T>> getByName(String name);

    /**
     * Gets all action types.
     *
     * @return all action types
     * @since 0.5.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<ActionType<?>> getActionTypes();
}
