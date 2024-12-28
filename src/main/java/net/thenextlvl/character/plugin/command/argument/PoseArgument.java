package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import org.bukkit.entity.Pose;

import java.util.Arrays;

public class PoseArgument extends WrappedArgumentType<String, Pose> {
    public PoseArgument() {
        super(StringArgumentType.word(), (reader, type) -> Pose.valueOf(type.toUpperCase()), (context, builder) -> {
            Arrays.stream(Pose.values())
                    .map(Pose::name)
                    .map(String::toLowerCase)
                    .filter(name -> name.contains(builder.getRemaining()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
