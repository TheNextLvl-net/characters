package net.thenextlvl.character.plugin.action;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.notification.Notification;
import net.thenextlvl.character.notification.NotificationTrigger;
import net.thenextlvl.character.notification.NotificationType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record SimpleNotification<T>(
        NotificationTrigger trigger,
        NotificationType<T> type,
        T input
) implements Notification<T> {
    @Override
    public void send(final Player player, final Character character, final ClickAction<?> action) {
        type.getSender().send(player, character, action, input);
    }
}
