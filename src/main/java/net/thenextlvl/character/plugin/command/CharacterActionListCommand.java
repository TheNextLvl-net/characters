package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
final class CharacterActionListCommand extends SimpleCommand {
    private CharacterActionListCommand(CharacterPlugin plugin) {
        super(plugin, "list", "characters.command.action.list");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionListCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
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
                Placeholder.parsed("action_type", action.getActionType().name()),
                Placeholder.parsed("character", character.getName()),
                Placeholder.parsed("action", name)));
        return SINGLE_SUCCESS;
    }
}
