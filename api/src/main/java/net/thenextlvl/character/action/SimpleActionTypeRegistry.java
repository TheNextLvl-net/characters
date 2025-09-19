package net.thenextlvl.character.action;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NullMarked
final class SimpleActionTypeRegistry implements ActionTypeRegistry {
    public static final ActionTypeRegistry INSTANCE = new SimpleActionTypeRegistry();

    private final Set<ActionType<?>> actionTypes = new HashSet<>();

    @Override
    public boolean register(ActionType<?> type) {
        return actionTypes.add(type);
    }

    @Override
    public boolean isRegistered(ActionType<?> type) {
        return actionTypes.contains(type);
    }

    @Override
    public boolean unregister(ActionType<?> type) {
        return actionTypes.remove(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<ActionType<T>> getByName(String name) {
        return actionTypes.stream()
                .filter(actionType -> actionType.name().equals(name))
                .map(actionType -> (ActionType<T>) actionType)
                .findAny();
    }

    @Override
    public @Unmodifiable Set<ActionType<?>> getActionTypes() {
        return Set.copyOf(actionTypes);
    }
}
