package net.thenextlvl.character.plugin.command.goal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
final class CharacterGoalAddCommand extends BrigadierCommand {
    private CharacterGoalAddCommand(final CharacterPlugin plugin) {
        super(plugin, "add", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterGoalAddCommand(plugin);
        return command.create().then(characterArgument(plugin)); // todo: implement
    }
}
