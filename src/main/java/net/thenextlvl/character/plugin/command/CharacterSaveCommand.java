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

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
public class CharacterSaveCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("save").then(characterArgument(plugin)
                .executes(context -> save(context, plugin)));
    }

    private static int save(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var sender = context.getSource().getSender();
        var success = character.isPersistent() && character.persist();
        var message = !character.isPersistent() ? "character.not_persistent"
                : success ? "character.persisted" : "character.persisted.failed";
        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
