package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class CharacterStringActionCommand extends CharacterActionCommand<String> {
    private final String argument;

    protected CharacterStringActionCommand(final CharacterPlugin plugin, final ActionType<String> actionType, final String name, final String argument) {
        super(plugin, actionType, name);
        this.argument = argument;
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> create() {
        final var argument = Commands.argument(this.argument, StringArgumentType.greedyString());
        return super.create().then(argument.executes(this));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        return addAction(context, context.getArgument(argument, String.class));
    }
}
