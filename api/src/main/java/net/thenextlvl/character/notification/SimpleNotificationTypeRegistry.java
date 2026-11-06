package net.thenextlvl.character.notification;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

final class SimpleNotificationTypeRegistry implements NotificationTypeRegistry {
    public static final SimpleNotificationTypeRegistry INSTANCE = new SimpleNotificationTypeRegistry();

    private final Set<NotificationType<?>> types = new CopyOnWriteArraySet<>(Set.of(
            NotificationType.sound(),
            NotificationType.message(),
            NotificationType.title(),
            NotificationType.actionbar()
    ));

    @Override
    public boolean register(final NotificationType<?> type) {
        return !isRegistered(type.getName()) && types.add(type);
    }

    @Override
    public boolean isRegistered(final NotificationType<?> type) {
        return types.contains(type);
    }

    @Override
    public boolean isRegistered(final String name) {
        return types.stream().anyMatch(type -> type.getName().equals(name));
    }

    @Override
    public boolean unregister(final NotificationType<?> type) {
        return types.remove(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<NotificationType<T>> getByName(final String name) {
        return types.stream()
                .filter(type -> type.getName().equals(name))
                .map(type -> (NotificationType<T>) type)
                .findAny();
    }

    @Override
    public @Unmodifiable Set<NotificationType<?>> getNotificationTypes() {
        return Set.copyOf(types);
    }
}
