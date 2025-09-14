package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

// todo: split up into multiple commands
@NullMarked
final class CharacterGoalCommand extends BrigadierCommand {
    private CharacterGoalCommand(CharacterPlugin plugin) {
        super(plugin, "goal", "characters.command.goal");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterGoalCommand(plugin);
        return command.create()
                .then(command.add())
                .then(command.remove());
    }

    private ArgumentBuilder<CommandSourceStack, ?> add() {
        // todo: implement
        return Commands.literal("add").then(characterArgument(plugin));
    }

    private ArgumentBuilder<CommandSourceStack, ?> remove() {
        // todo: implement
        return Commands.literal("remove").then(characterArgument(plugin));
    }
}
