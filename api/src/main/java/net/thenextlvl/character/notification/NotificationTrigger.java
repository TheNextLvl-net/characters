package net.thenextlvl.character.notification;

import net.kyori.adventure.key.KeyPattern;
import org.jetbrains.annotations.Contract;

/**
 * When a notification is sent.
 *
 * @see NotificationTriggerRegistry
 * @since 0.9.0
 */
public sealed interface NotificationTrigger permits SimpleNotificationTrigger {
    /**
     * The action was invoked.
     *
     * @return the trigger
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationTrigger success() {
        return SimpleNotificationTrigger.SUCCESS;
    }

    /**
     * The action was used while it is on cooldown.
     *
     * @return the trigger
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationTrigger cooldown() {
        return SimpleNotificationTrigger.COOLDOWN;
    }

    /**
     * The action was used by a player without its permission.
     *
     * @return the trigger
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationTrigger noPermission() {
        return SimpleNotificationTrigger.NO_PERMISSION;
    }

    /**
     * Returns the name of this trigger.
     *
     * @return the name
     * @since 0.9.0
     */
    @KeyPattern.Value
    @Contract(pure = true)
    String getName();

    /**
     * Creates a new trigger, which has to be registered to be persisted.
     *
     * @param name the name
     * @return the trigger
     * @see NotificationTriggerRegistry#register(NotificationTrigger)
     * @since 0.9.0
     */
    @Contract(value = "_ -> new", pure = true)
    static NotificationTrigger create(@KeyPattern.Value final String name) {
        return new SimpleNotificationTrigger(name);
    }
}
