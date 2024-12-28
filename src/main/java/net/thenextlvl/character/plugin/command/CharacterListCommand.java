package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class CharacterListCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("list").executes(context -> list(context, plugin));
    }

    private static int list(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var characters = plugin.characterController().getCharacters();
        if (characters.isEmpty()) plugin.bundle().sendMessage(sender, "character.list.empty");
        else characters.forEach(character -> plugin.bundle().sendMessage(sender, "character.list",
                Placeholder.parsed("character", character.getName()),
                Placeholder.unparsed("type", character.getType().key().asString())));
        return Command.SINGLE_SUCCESS;
    }
}
