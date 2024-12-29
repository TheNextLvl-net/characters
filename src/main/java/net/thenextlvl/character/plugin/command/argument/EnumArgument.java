package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;

import java.util.Arrays;

public class EnumArgument<T extends Enum<T>> extends WrappedArgumentType<String, T> {
    public EnumArgument(Class<T> enumClass) {
        super(StringArgumentType.word(),
                (reader, type) -> Enum.valueOf(enumClass, type.toUpperCase()),
                (context, builder) -> {
                    Arrays.stream(enumClass.getEnumConstants())
                            .map(Enum::name)
                            .map(String::toLowerCase)
                            .filter(name -> name.contains(builder.getRemaining()))
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }
}
