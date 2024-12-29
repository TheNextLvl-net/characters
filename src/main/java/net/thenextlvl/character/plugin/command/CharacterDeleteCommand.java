package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterDeleteCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("delete").then(characterArgument(plugin)
                .executes(context -> delete(context, plugin)));
    }

    private static int delete(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var name = context.getArgument("character", String.class);
        var character = plugin.characterController().getCharacter(name).orElse(null);

        if (character != null) character.remove();

        var sender = context.getSource().getSender();
        var message = character != null ? "character.deleted" : "character.not_found";
        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("name", name));

        return character != null ? Command.SINGLE_SUCCESS : 0;
    }
}
