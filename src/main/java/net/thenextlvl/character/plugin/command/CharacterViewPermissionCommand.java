package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterViewPermissionCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("view-permission")
                .then(remove(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> remove(CharacterPlugin plugin) {
        return Commands.literal("remove").then(characterArgument(plugin)
                .executes(context -> remove(context, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin).then(permissionArgument()
                .executes(context -> set(context, plugin))));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> permissionArgument() {
        return Commands.argument("permission", StringArgumentType.string());
    }

    private static int remove(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var success = set(context, null, plugin);
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static int set(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var permission = context.getArgument("permission", String.class);
        var success = set(context, permission, plugin);
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static boolean set(CommandContext<CommandSourceStack> context, @Nullable String viewPermission, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.setViewPermission(viewPermission);
        var message = !success ? "nothing.changed" : viewPermission != null
                ? "character.view-permission.set" : "character.view-permission.removed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("permission", String.valueOf(viewPermission)));
        return success;
    }
}
