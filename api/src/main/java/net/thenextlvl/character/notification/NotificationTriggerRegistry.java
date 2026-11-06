package net.thenextlvl.character.notification;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/**
 * All known notification triggers, which are persisted by name.
 *
 * @since 0.9.0
 */
public sealed interface NotificationTriggerRegistry permits SimpleNotificationTriggerRegistry {
    /**
     * Returns the registry.
     *
     * @return the registry
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationTriggerRegistry registry() {
        return SimpleNotificationTriggerRegistry.INSTANCE;
    }

    /**
     * Registers the given trigger.
     *
     * @param trigger the trigger
     * @return {@code true} if the trigger was registered
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean register(NotificationTrigger trigger);

    /**
     * Checks if the given trigger is registered.
     *
     * @param trigger the trigger
     * @return {@code true} if the trigger is registered
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isRegistered(NotificationTrigger trigger);

    /**
     * Checks if a trigger with the given name is registered.
     *
     * @param name the name
     * @return {@code true} if such a trigger is registered
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isRegistered(String name);

    /**
     * Unregisters the given trigger.
     *
     * @param trigger the trigger
     * @return {@code true} if the trigger was unregistered
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean unregister(NotificationTrigger trigger);

    /**
     * Returns the trigger with the given name.
     *
     * @param name the name
     * @return the trigger, or empty if none is registered with the name
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<NotificationTrigger> getByName(String name);

    /**
     * Returns all registered triggers.
     *
     * @return the triggers
     * @since 0.9.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<NotificationTrigger> getTriggers();
}
