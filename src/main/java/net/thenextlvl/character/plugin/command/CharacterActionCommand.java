package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.suggestion.CharacterActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
class CharacterActionCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("action")
                .then(CharacterActionAddCommand.create(plugin))
                .then(CharacterActionCooldownCommand.create(plugin))
                .then(CharacterActionListCommand.create(plugin))
                .then(CharacterActionPermissionCommand.create(plugin))
                .then(CharacterActionRemoveCommand.create(plugin));
    }

    static ArgumentBuilder<CommandSourceStack, ?> actionArgument(CharacterPlugin plugin) {
        return Commands.argument("action", StringArgumentType.word())
                .suggests(new CharacterActionSuggestionProvider());
    }
}
