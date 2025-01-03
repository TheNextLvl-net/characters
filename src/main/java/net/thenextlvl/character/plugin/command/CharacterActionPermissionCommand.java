package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static net.thenextlvl.character.plugin.command.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterActionPermissionCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("permission")
                .then(remove(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> remove(CharacterPlugin plugin) {
        return Commands.literal("remove").then(characterArgument(plugin).then(actionArgument(plugin)
                .executes(context -> set(context, null, plugin))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin).then(actionArgument(plugin)
                .then(permissionArgument().executes(context -> set(context, plugin)))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> permissionArgument() {
        return Commands.argument("permission", StringArgumentType.string());
    }

    private static int set(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var permission = context.getArgument("permission", String.class);
        return set(context, permission, plugin);
    }

    private static int set(CommandContext<CommandSourceStack> context, @Nullable String permission, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("character", String.class);
        var character = plugin.characterController().getCharacter(name).orElse(null);
        if (character == null) {
            plugin.bundle().sendMessage(sender, "character.not_found", Placeholder.unparsed("name", name));
            return 0;
        }
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName);

        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", name),
                    Placeholder.unparsed("name", actionName));
            return 0;
        }

        var success = !Objects.equals(action.getPermission(), permission);
        if (success) action.setPermission(permission);

        var message = success ? permission != null
                ? "character.action.permission.set" : "character.action.permission.removed"
                : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", name),
                Placeholder.unparsed("permission", String.valueOf(permission)));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
