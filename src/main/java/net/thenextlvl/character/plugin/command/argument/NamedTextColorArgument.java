package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import net.kyori.adventure.text.format.NamedTextColor;

public class NamedTextColorArgument extends WrappedArgumentType<String, NamedTextColor> {
    public NamedTextColorArgument() {
        super(StringArgumentType.word(), (reader, type) ->
                        NamedTextColor.NAMES.valueOrThrow(type),
                (context, builder) -> {
                    NamedTextColor.NAMES.keys().forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }
}
