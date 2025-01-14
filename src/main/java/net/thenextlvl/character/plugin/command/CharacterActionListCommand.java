package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterActionListCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("list").then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .executes(context -> list(context, plugin)));
    }

    private static int list(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        if (character.getActions().isEmpty()) {
            plugin.bundle().sendMessage(sender, "character.action.list.empty",
                    Placeholder.unparsed("character", character.getName()));
            return 0;
        }
        plugin.bundle().sendMessage(sender, "character.action.list.header",
                Placeholder.parsed("character", character.getName()));
        character.getActions().forEach((name, action) -> plugin.bundle().sendMessage(sender, "character.action.list",
                Placeholder.parsed("cooldown", action.getCooldown().toString()),
                Placeholder.parsed("permission", action.getPermission() == null ? "undefined" : action.getPermission()),
                Placeholder.parsed("action_type", action.getActionType().name()),
                Placeholder.parsed("character", character.getName()),
                Placeholder.parsed("action", name)));
        return Command.SINGLE_SUCCESS;
    }
}
