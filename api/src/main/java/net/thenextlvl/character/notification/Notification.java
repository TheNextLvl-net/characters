package net.thenextlvl.character.notification;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * A notification sent to a player when its trigger fires.
 *
 * @param <T> the input type
 * @see Notifications
 * @since 0.9.0
 */
@ApiStatus.NonExtendable
public interface Notification<T> {
    /**
     * Returns the trigger of this notification.
     *
     * @return the trigger
     * @since 0.9.0
     */
    @Contract(pure = true)
    NotificationTrigger trigger();

    /**
     * Returns the type of this notification.
     *
     * @return the type
     * @since 0.9.0
     */
    @Contract(pure = true)
    NotificationType<T> type();

    /**
     * Returns the input of this notification, like the message or sound.
     *
     * @return the input
     * @since 0.9.0
     */
    @Contract(pure = true)
    T input();

    /**
     * Sends this notification to the given player.
     *
     * @param player    the player
     * @param character the character the player interacted with
     * @param action    the action that triggered the notification
     * @since 0.9.0
     */
    void send(Player player, Character character, ClickAction<?> action);
}
