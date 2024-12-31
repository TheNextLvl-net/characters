package net.thenextlvl.character.action;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;

@NullMarked
public interface ActionTypeRegistry {
    <T> ActionType<T> register(ActionType<T> actionType);

    Optional<ActionType<?>> getByName(String name);

    @Unmodifiable
    Set<ActionType<?>> getActionTypes();

    boolean isRegistered(ActionType<?> actionType);

    boolean unregister(ActionType<?> actionType);
}
