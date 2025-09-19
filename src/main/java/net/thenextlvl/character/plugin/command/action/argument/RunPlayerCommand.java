package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RunPlayerCommand extends CharacterStringActionCommand {
    private RunPlayerCommand(CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().runCommand(), "run-command", "command");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return new RunPlayerCommand(plugin).create();
    }
}
