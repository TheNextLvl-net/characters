package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
        var name = context.getArgument("character", String.class);
        var character = plugin.characterController().getCharacter(name).orElse(null);

        if (character == null) {
            plugin.bundle().sendMessage(sender, "character.not_found", Placeholder.unparsed("name", name));
            return 0;
        }

        var action = context.getArgument("action", String.class);

        if (character.removeAction(action)) {
            plugin.bundle().sendMessage(sender, "character.action.removed",
                    Placeholder.unparsed("action", action),
                    Placeholder.unparsed("character", name));
            return Command.SINGLE_SUCCESS;
        }

        plugin.bundle().sendMessage(sender, "character.action.not_found",
                Placeholder.parsed("character", name),
                Placeholder.unparsed("name", action));
        return 0;
    }
}
