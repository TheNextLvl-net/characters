package net.thenextlvl.character.action;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;

/**
 * @since 0.5.0
 */
@NullMarked
public sealed interface ActionTypeRegistry permits SimpleActionTypeRegistry {
    @Contract(pure = true)
    static ActionTypeRegistry registry() {
        return SimpleActionTypeRegistry.INSTANCE;
    }

    @Contract(mutates = "this")
    boolean register(ActionType<?> type);

    @Contract(pure = true)
    boolean isRegistered(ActionType<?> type);

    @Contract(mutates = "this")
    boolean unregister(ActionType<?> type);

    @Contract(pure = true)
    <T> Optional<ActionType<T>> getByName(String name);

    @Unmodifiable
    @Contract(pure = true)
    Set<ActionType<?>> getActionTypes();
}
