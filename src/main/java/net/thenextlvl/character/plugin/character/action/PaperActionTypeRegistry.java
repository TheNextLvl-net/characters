package net.thenextlvl.character.plugin.character.action;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ActionTypeRegistry;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NullMarked
public class PaperActionTypeRegistry implements ActionTypeRegistry {
    private final Set<ActionType<?>> actionTypes = new HashSet<>();

    @Override
    public <T> ActionType<T> register(ActionType<T> actionType) {
        Preconditions.checkState(actionTypes.add(actionType), "Action type already registered");
        return actionType;
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
    public boolean isRegistered(ActionType<?> actionType) {
        return actionTypes.contains(actionType);
    }

    @Override
    public boolean unregister(ActionType<?> actionType) {
        return actionTypes.remove(actionType);
    }
}
