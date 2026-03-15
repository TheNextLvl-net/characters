package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RunConsoleCommand extends CharacterStringActionCommand {
    private RunConsoleCommand(final CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().runConsoleCommand(), "run-console-command", "command");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        return new RunConsoleCommand(plugin).create();
    }
}
