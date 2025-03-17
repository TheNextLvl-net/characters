package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
public class CharacterGoalCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("goal")
                .requires(source -> source.getSender().hasPermission("characters.command.goal"))
                .then(add(plugin))
                .then(remove(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> add(CharacterPlugin plugin) {
        // todo: implement
        return Commands.literal("add").then(characterArgument(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> remove(CharacterPlugin plugin) {
        // todo: implement
        return Commands.literal("remove").then(characterArgument(plugin));
    }
}
