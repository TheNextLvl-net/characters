package net.thenextlvl.character.notification;

import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.sound.Sound;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

/**
 * How a notification is sent.
 * <p>
 * Messages and titles are in the MiniMessage format and support the placeholders
 * {@code <player>}, {@code <character>}, {@code <permission>},
 * {@code <cooldown>} and {@code <remaining-cooldown>}, the cooldowns in seconds.
 *
 * @param <T> the input type
 * @see NotificationTypeRegistry
 * @since 0.9.0
 */
public sealed interface NotificationType<T> permits SimpleNotificationType {
    /**
     * Plays a sound.
     *
     * @return the type
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationType<Sound> sound() {
        return SimpleNotificationType.PLAY_SOUND;
    }

    /**
     * Sends a chat message.
     *
     * @return the type
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationType<String> message() {
        return SimpleNotificationType.MESSAGE;
    }

    /**
     * Shows a title.
     *
     * @return the type
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationType<UnparsedTitle> title() {
        return SimpleNotificationType.TITLE;
    }

    /**
     * Sends an action bar message.
     *
     * @return the type
     * @since 0.9.0
     */
    @Contract(pure = true)
    static NotificationType<String> actionbar() {
        return SimpleNotificationType.ACTIONBAR;
    }

    /**
     * Returns the input type.
     *
     * @return the input type
     * @since 0.9.0
     */
    @Contract(pure = true)
    Class<T> getType();

    /**
     * Returns the name of this type.
     *
     * @return the name
     * @since 0.9.0
     */
    @KeyPattern.Value
    @Contract(pure = true)
    String getName();

    /**
     * Returns the sender of this type.
     *
     * @return the sender
     * @since 0.9.0
     */
    @Contract(pure = true)
    Sender<T> getSender();

    /**
     * Sends a notification.
     *
     * @param <T> the input type
     * @since 0.9.0
     */
    @FunctionalInterface
    interface Sender<T> {
        /**
         * Sends the notification to the given player.
         *
         * @param player    the player
         * @param character the character the player interacted with
         * @param action    the action that triggered the notification
         * @param input     the input
         * @since 0.9.0
         */
        void send(Player player, Character character, ClickAction<?> action, T input);
    }

    /**
     * Creates a new type, which has to be registered to be persisted.
     *
     * @param name   the name
     * @param type   the input type
     * @param sender the sender
     * @param <T>    the input type
     * @return the type
     * @see NotificationTypeRegistry#register(NotificationType)
     * @since 0.9.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> NotificationType<T> create(@KeyPattern.Value final String name, final Class<T> type, final Sender<T> sender) {
        return new SimpleNotificationType<>(name, type, sender);
    }
}
