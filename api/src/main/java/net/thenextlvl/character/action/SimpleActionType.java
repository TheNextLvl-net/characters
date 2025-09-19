package net.thenextlvl.character.action;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.character.Character;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiPredicate;

@NullMarked
final class SimpleActionType<T> implements ActionType<T> {
    private final @KeyPattern.Value String name;
    private final Class<T> type;
    private final Action<T> action;
    private final @Nullable BiPredicate<T, Character<?>> applicable;

    SimpleActionType(
            @KeyPattern.Value String name, Class<T> type, Action<T> action,
            @Nullable BiPredicate<T, Character<?>> applicable
    ) {
        this.name = name;
        this.type = type;
        this.action = action;
        this.applicable = applicable;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public @KeyPattern.Value String name() {
        return name;
    }

    @Override
    public Action<T> action() {
        return action;
    }

    @Override
    public boolean isApplicable(T input, Character<?> character) {
        return applicable == null || applicable.test(input, character);
    }

    public static final class Builder<T> implements ActionType.Builder<T> {
        private final Class<T> type;
        private final @KeyPattern.Value String name;
        private @Nullable Action<T> action;
        private @Nullable BiPredicate<T, Character<?>> applicable = null;

        Builder(Class<T> type, @KeyPattern.Value String name) {
            this.type = type;
            this.name = name;
        }

        public Builder<T> action(@Nullable Action<T> action) {
            this.action = action;
            return this;
        }

        public Builder<T> applicable(BiPredicate<T, Character<?>> applicable) {
            this.applicable = applicable;
            return this;
        }

        public ActionType<T> build() throws IllegalArgumentException {
            Preconditions.checkArgument(action != null, "action is null");
            return new SimpleActionType<>(name, type, action, applicable);
        }
    }
}
