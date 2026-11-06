package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import core.paper.brigadier.arguments.EnumArgumentType;
import core.paper.brigadier.arguments.codecs.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.CharacterAnimation;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlayAnimationCommand extends CharacterActionCommand<CharacterAnimation> {
    private PlayAnimationCommand(final CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().playAnimation(), "play-animation");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new PlayAnimationCommand(plugin);
        return command.create().then(animationArgument().executes(command));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> animationArgument() {
        return Commands.argument("animation", EnumArgumentType.of(CharacterAnimation.class, EnumStringCodec.lowerHyphen()));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return addAction(context, context.getArgument("animation", CharacterAnimation.class));
    }
}
