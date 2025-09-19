package net.thenextlvl.character.plugin.command.goal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CharacterGoalCommand extends BrigadierCommand {
    private CharacterGoalCommand(CharacterPlugin plugin) {
        super(plugin, "goal", "characters.command.goal");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterGoalCommand(plugin);
        return command.create()
                .then(CharacterGoalAddCommand.create(plugin))
                .then(CharacterGoalRemoveCommand.create(plugin));
    }
}
