package net.thenextlvl.character.plugin.character.action;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ActionTypeProvider;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NullMarked
public final class PaperActionTypeProvider implements ActionTypeProvider {
    private final Set<ActionType<?>> actionTypes = new HashSet<>();

    @Override
    public <T> ActionType<T> register(ActionType<T> type) {
        Preconditions.checkState(actionTypes.add(type), "Action type already registered");
        return type;
    }

    @Override
    public Optional<ActionType<?>> getByName(String name) {
        return actionTypes.stream().filter(actionType -> actionType.name().equals(name)).findAny();
    }

    @Override
    public @Unmodifiable Set<ActionType<?>> getActionTypes() {
        return Set.copyOf(actionTypes);
    }

    @Override
    public boolean isRegistered(ActionType<?> type) {
        return actionTypes.contains(type);
    }

    @Override
    public boolean unregister(ActionType<?> type) {
        return actionTypes.remove(type);
    }
}
