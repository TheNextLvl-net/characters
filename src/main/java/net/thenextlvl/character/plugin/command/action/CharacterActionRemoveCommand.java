package net.thenextlvl.character.plugin.command.action;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.action.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
final class CharacterActionRemoveCommand extends SimpleCommand {
    private CharacterActionRemoveCommand(CharacterPlugin plugin) {
        super(plugin, "remove", "characters.command.action.remove");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionRemoveCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin).executes(command)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);
        var action = context.getArgument("action", String.class);

        var success = character.removeAction(action);
        var message = success ? "character.action.removed" : "character.action.not_found";
        
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("character", character.getName()),
                Placeholder.parsed("action", action));
        return success ? SINGLE_SUCCESS : 0;
    }
}
