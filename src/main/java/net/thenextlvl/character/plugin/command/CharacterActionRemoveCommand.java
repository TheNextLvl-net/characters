package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterActionRemoveCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("remove").then(characterArgument(plugin).then(actionArgument(plugin)
                .executes(context -> remove(context, plugin))));
    }

    private static int remove(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var action = context.getArgument("action", String.class);

        if (character.removeAction(action)) {
            plugin.bundle().sendMessage(sender, "character.action.removed",
                    Placeholder.unparsed("action", action),
                    Placeholder.unparsed("character", character.getName()));
            return Command.SINGLE_SUCCESS;
        }

        plugin.bundle().sendMessage(sender, "character.action.not_found",
                Placeholder.parsed("character", character.getName()),
                Placeholder.unparsed("name", action));
        return 0;
    }
}
