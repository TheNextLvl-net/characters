package net.thenextlvl.character.plugin.action;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.notification.Notification;
import net.thenextlvl.character.notification.NotificationTrigger;
import net.thenextlvl.character.notification.NotificationType;
import net.thenextlvl.character.notification.Notifications;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@NullMarked
public final class SimpleNotifications implements Notifications {
    private final List<Notification<?>> notifications = new CopyOnWriteArrayList<>();

    @Override
    public Stream<Notification<?>> stream() {
        return notifications.stream();
    }

    @Override
    public Stream<Notification<?>> findByTrigger(final NotificationTrigger trigger) {
        return stream().filter(notification -> notification.trigger().equals(trigger));
    }

    @Override
    public boolean isEmpty() {
        return notifications.isEmpty();
    }

    @Override
    public int size() {
        return notifications.size();
    }

    @Override
    public void trigger(final NotificationTrigger trigger, final Player player, final Character character, final ClickAction<?> action) {
        findByTrigger(trigger).forEach(notification -> notification.send(player, character, action));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final NotificationTrigger trigger, final NotificationType<T> type) {
        return find(trigger, type).map(notification -> (T) notification.input());
    }

    @Override
    public synchronized <T> boolean set(final NotificationTrigger trigger, final NotificationType<T> type, final T input) {
        final var existing = find(trigger, type);
        if (existing.isPresent() && Objects.equals(existing.get().input(), input)) return false;
        existing.ifPresent(notifications::remove);
        notifications.add(new SimpleNotification<>(trigger, type, input));
        return true;
    }

    @Override
    public synchronized boolean remove(final NotificationTrigger trigger, final NotificationType<?> type) {
        return find(trigger, type).map(notifications::remove).orElse(false);
    }

    @Override
    public synchronized void copyFrom(final Notifications other) {
        if (other == this) return;
        notifications.clear();
        other.forEach(notifications::add);
    }

    private Optional<Notification<?>> find(final NotificationTrigger trigger, final NotificationType<?> type) {
        return findByTrigger(trigger).filter(notification -> notification.type().equals(type)).findAny();
    }

    @Override
    public Iterator<Notification<?>> iterator() {
        return notifications.iterator();
    }
}
