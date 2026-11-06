package net.thenextlvl.character.plugin.command.action;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.plugin.command.suggestion.CharacterActionSuggestionProvider;
import net.thenextlvl.character.plugin.command.action.notification.CharacterActionNotificationCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CharacterActionCommand extends BrigadierCommand {
    private CharacterActionCommand(final CharacterPlugin plugin) {
        super(plugin, "action", "characters.command.action");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        return new CharacterActionCommand(plugin).create()
                .then(CharacterActionAddCommand.create(plugin))
                .then(CharacterActionChanceCommand.create(plugin))
                .then(CharacterActionCooldownCommand.create(plugin))
                .then(CharacterActionListCommand.create(plugin))
                .then(CharacterActionNotificationCommand.create(plugin))
                .then(CharacterActionPermissionCommand.create(plugin))
                .then(CharacterActionRemoveCommand.create(plugin));
    }

    public static ArgumentBuilder<CommandSourceStack, ?> actionArgument(final CharacterPlugin plugin) {
        return Commands.argument("action", StringArgumentType.word())
                .suggests(new CharacterActionSuggestionProvider());
    }
}
