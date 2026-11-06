package net.thenextlvl.character.notification;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/**
 * All known notification types, which are persisted by name.
 *
 * @since 0.9.0
 */
public sealed interface NotificationTypeRegistry permits SimpleNotificationTypeRegistry {
    /**
     * Returns the registry.
     *
     * @return the registry
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationTypeRegistry registry() {
        return SimpleNotificationTypeRegistry.INSTANCE;
    }

    /**
     * Registers the given type.
     *
     * @param type the type
     * @return {@code true} if the type was registered
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean register(NotificationType<?> type);

    /**
     * Checks if the given type is registered.
     *
     * @param type the type
     * @return {@code true} if the type is registered
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isRegistered(NotificationType<?> type);

    /**
     * Checks if a type with the given name is registered.
     *
     * @param name the name
     * @return {@code true} if such a type is registered
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isRegistered(String name);

    /**
     * Unregisters the given type.
     *
     * @param type the type
     * @return {@code true} if the type was unregistered
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean unregister(NotificationType<?> type);

    /**
     * Returns the type with the given name.
     *
     * @param name the name
     * @param <T>  the input type
     * @return the type, or empty if none is registered with the name
     * @since 0.9.0
     */
    @Contract(pure = true)
    <T> Optional<NotificationType<T>> getByName(String name);

    /**
     * Returns all registered types.
     *
     * @return the types
     * @since 0.9.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<NotificationType<?>> getNotificationTypes();
}
