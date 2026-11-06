package net.thenextlvl.character.notification;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The notifications of a click action, at most one of every type per trigger.
 *
 * @see ClickAction#getNotifications()
 * @since 0.9.0
 */
@ApiStatus.NonExtendable
public interface Notifications extends Iterable<Notification<?>> {
    /**
     * Returns a stream of all notifications.
     *
     * @return the notifications
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Notification<?>> stream();

    /**
     * Returns the notifications of the given trigger.
     *
     * @param trigger the trigger
     * @return the notifications of the trigger
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Notification<?>> findByTrigger(NotificationTrigger trigger);

    /**
     * Checks if there are no notifications.
     *
     * @return {@code true} if there are no notifications
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isEmpty();

    /**
     * Returns the number of notifications.
     *
     * @return the number of notifications
     * @since 0.9.0
     */
    @Contract(pure = true)
    int size();

    /**
     * Sends all notifications of the given trigger to the given player.
     *
     * @param trigger   the trigger
     * @param player    the player
     * @param character the character the player interacted with
     * @param action    the action that triggered the notifications
     * @since 0.9.0
     */
    void trigger(NotificationTrigger trigger, Player player, Character character, ClickAction<?> action);

    /**
     * Returns the input of the notification of the given trigger and type.
     *
     * @param trigger the trigger
     * @param type    the type
     * @param <T>     the input type
     * @return the input, or empty if there is no such notification
     * @since 0.9.0
     */
    @Contract(pure = true)
    <T> Optional<T> get(NotificationTrigger trigger, NotificationType<T> type);

    /**
     * Sets the notification of the given trigger and type, replacing the previous one.
     *
     * @param trigger the trigger
     * @param type    the type
     * @param input   the input
     * @param <T>     the input type
     * @return {@code true} if the notification was changed
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    <T> boolean set(NotificationTrigger trigger, NotificationType<T> type, T input);

    /**
     * Removes the notification of the given trigger and type.
     *
     * @param trigger the trigger
     * @param type    the type
     * @return {@code true} if a notification was removed
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean remove(NotificationTrigger trigger, NotificationType<?> type);

    /**
     * Replaces all notifications with the ones of the given notifications.
     *
     * @param other the notifications to copy
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    void copyFrom(Notifications other);
}
