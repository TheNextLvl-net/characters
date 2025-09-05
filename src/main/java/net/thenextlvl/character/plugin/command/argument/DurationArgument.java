package net.thenextlvl.character.plugin.command.argument;

import core.paper.command.WrappedArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.util.Tick;

import java.time.Duration;

public class DurationArgument extends WrappedArgumentType<Integer, Duration> {
    public DurationArgument() {
        super(ArgumentTypes.time(0), (reader, type) -> Tick.of(type));
    }
}
