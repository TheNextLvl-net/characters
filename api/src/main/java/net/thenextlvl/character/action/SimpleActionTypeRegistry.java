package net.thenextlvl.character.action;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NullMarked
final class SimpleActionTypeRegistry implements ActionTypeRegistry {
    public static final ActionTypeRegistry INSTANCE = new SimpleActionTypeRegistry();

    private final Set<ActionType<?>> actionTypes = new HashSet<>(Set.of(
            ActionTypes.types().sendActionbar(),
            ActionTypes.types().sendMessage(),
            ActionTypes.types().sendEntityEffect(),
            ActionTypes.types().transfer(),
            ActionTypes.types().teleport(),
            ActionTypes.types().playSound(),
            ActionTypes.types().runConsoleCommand(),
            ActionTypes.types().runCommand(),
            ActionTypes.types().sendTitle(),
            ActionTypes.types().connect()
    ));

    @Override
    public boolean register(ActionType<?> type) {
        return !isRegistered(type.name()) && actionTypes.add(type);
    }

    @Override
    public boolean isRegistered(ActionType<?> type) {
        return actionTypes.contains(type);
    }

    @Override
    public boolean isRegistered(String name) {
        return actionTypes.stream().anyMatch(actionType -> actionType.name().equals(name));
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
