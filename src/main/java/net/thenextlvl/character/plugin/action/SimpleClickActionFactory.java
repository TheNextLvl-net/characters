package net.thenextlvl.character.plugin.action;

import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.action.ClickActionFactory;
import net.thenextlvl.character.action.ClickType;
import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;
import java.util.function.Consumer;

@NullMarked
public final class SimpleClickActionFactory implements ClickActionFactory {
    public static final ClickActionFactory INSTANCE = new SimpleClickActionFactory();

    private SimpleClickActionFactory() {
    }

    @Override
    public <T> ClickAction<T> create(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input) {
        return new SimpleClickAction<>(actionType, clickTypes, input);
    }

    @Override
    public <T> ClickAction<T> create(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input, final Consumer<ClickAction<T>> configurator) {
        final var action = create(actionType, clickTypes, input);
        configurator.accept(action);
        return action;
    }
}
