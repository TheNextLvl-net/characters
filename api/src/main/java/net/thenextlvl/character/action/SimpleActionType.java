package net.thenextlvl.character.action;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.character.Character;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
final class SimpleActionType<T> implements ActionType<T> {
    private final @KeyPattern.Value String name;
    private final Class<T> type;
    private final Action<T> action;
    private final @Nullable BiPredicate<T, Character<?>> applicable;
    private final Supplier<ArgumentBuilder<CommandSourceStack, ?>> argumentTree;
    private final Function<CommandContext<CommandSourceStack>, T> parser;

    SimpleActionType(
            @KeyPattern.Value String name, Class<T> type, Action<T> action,
            @Nullable BiPredicate<T, Character<?>> applicable,
            Supplier<ArgumentBuilder<CommandSourceStack, ?>> argumentTree,
            Function<CommandContext<CommandSourceStack>, T> parser
    ) {
        this.name = name;
        this.type = type;
        this.action = action;
        this.applicable = applicable;
        this.argumentTree = argumentTree;
        this.parser = parser;
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
    public Supplier<ArgumentBuilder<CommandSourceStack, ?>> argumentTree() {
        return argumentTree;
    }

    @Override
    public Function<CommandContext<CommandSourceStack>, T> parser() {
        return parser;
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
        private @Nullable Supplier<ArgumentBuilder<CommandSourceStack, ?>> argumentTree;
        private @Nullable Function<CommandContext<CommandSourceStack>, T> parser;

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

        public Builder<T> argumentTree(@Nullable Supplier<ArgumentBuilder<CommandSourceStack, ?>> argumentTree) {
            this.argumentTree = argumentTree;
            return this;
        }

        public Builder<T> parser(@Nullable Function<CommandContext<CommandSourceStack>, T> parser) {
            this.parser = parser;
            return this;
        }

        public ActionType<T> build() throws IllegalArgumentException {
            Preconditions.checkArgument(action != null, "action is null");
            Preconditions.checkArgument(argumentTree != null, "argumentTree is null");
            Preconditions.checkArgument(parser != null, "parser is null");
            return new SimpleActionType<>(name, type, action, applicable, argumentTree, parser);
        }
    }
}
