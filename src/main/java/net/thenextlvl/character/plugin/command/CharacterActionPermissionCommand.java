package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static net.thenextlvl.character.plugin.command.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.permissionArgument;

// todo: rework syntax
@NullMarked
final class CharacterActionPermissionCommand extends BrigadierCommand {
    private CharacterActionPermissionCommand(CharacterPlugin plugin) {
        super(plugin, "permission", "characters.command.action.permission");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionPermissionCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin)
                        .then(command.remove())
                        .then(command.set())
                        .executes(command::get)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> remove() {
        return Commands.literal("remove").executes(this::set);
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set").then(permissionArgument(plugin).executes(this::set));
    }

    private int get(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName).orElse(null);
        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("action", actionName));
            return 0;
        }
        var message = action.getPermission() != null ? "character.action.permission" : "character.action.permission.none";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("permission", String.valueOf(action.getPermission())),
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private int set(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName).orElse(null);
        var permission = tryGetArgument(context, "permission", String.class).orElse(null);

        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("action", actionName));
            return 0;
        }

        var success = !Objects.equals(action.getPermission(), permission);
        if (success) action.setPermission(permission);

        var message = success ? permission != null
                ? "character.action.permission.set" : "character.action.permission.removed"
                : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("permission", String.valueOf(permission)));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
