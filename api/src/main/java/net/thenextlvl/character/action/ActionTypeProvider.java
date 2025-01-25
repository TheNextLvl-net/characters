package net.thenextlvl.character.action;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;

@NullMarked
public interface ActionTypeProvider {
    <T> ActionType<T> register(ActionType<T> type);

    Optional<ActionType<?>> getByName(String name);

    @Unmodifiable
    Set<ActionType<?>> getActionTypes();

    boolean isRegistered(ActionType<?> type);

    boolean unregister(ActionType<?> type);
}
