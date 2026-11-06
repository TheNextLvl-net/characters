package net.thenextlvl.character.notification;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

final class SimpleNotificationTriggerRegistry implements NotificationTriggerRegistry {
    public static final SimpleNotificationTriggerRegistry INSTANCE = new SimpleNotificationTriggerRegistry();

    private final Set<NotificationTrigger> triggers = new CopyOnWriteArraySet<>(Set.of(
            NotificationTrigger.success(),
            NotificationTrigger.cooldown(),
            NotificationTrigger.noPermission()
    ));

    @Override
    public boolean register(final NotificationTrigger trigger) {
        return !isRegistered(trigger.getName()) && triggers.add(trigger);
    }

    @Override
    public boolean isRegistered(final NotificationTrigger trigger) {
        return triggers.contains(trigger);
    }

    @Override
    public boolean isRegistered(final String name) {
        return triggers.stream().anyMatch(trigger -> trigger.getName().equals(name));
    }

    @Override
    public boolean unregister(final NotificationTrigger trigger) {
        return triggers.remove(trigger);
    }

    @Override
    public Optional<NotificationTrigger> getByName(final String name) {
        return triggers.stream().filter(trigger -> trigger.getName().equals(name)).findAny();
    }

    @Override
    public @Unmodifiable Set<NotificationTrigger> getTriggers() {
        return Set.copyOf(triggers);
    }
}
