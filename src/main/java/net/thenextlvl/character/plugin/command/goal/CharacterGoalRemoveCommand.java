package net.thenextlvl.character.plugin.command.goal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
final class CharacterGoalRemoveCommand extends BrigadierCommand {
    private CharacterGoalRemoveCommand(CharacterPlugin plugin) {
        super(plugin, "remove", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterGoalRemoveCommand(plugin);
        return command.create().then(characterArgument(plugin)); // todo: implement
    }
}
