package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.WrappedArgumentType;

import java.util.Arrays;
import java.util.function.BiPredicate;

public class EnumArgument<T extends Enum<T>> extends WrappedArgumentType<String, T> {
    public EnumArgument(Class<T> enumClass) {
        this(enumClass, (c, e) -> true);
    }

    public EnumArgument(Class<T> enumClass, BiPredicate<CommandContext<?>, T> filter) {
        super(StringArgumentType.word(),
                (reader, type) -> Enum.valueOf(enumClass, type.toUpperCase()),
                (context, builder) -> {
                    Arrays.stream(enumClass.getEnumConstants())
                            .filter(e -> filter.test(context, e))
                            .map(Enum::name)
                            .map(String::toLowerCase)
                            .filter(name -> name.contains(builder.getRemaining()))
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }
}
