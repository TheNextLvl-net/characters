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
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

// todo: split up into multiple commands
@NullMarked
final class CharacterViewPermissionCommand extends BrigadierCommand {
    private CharacterViewPermissionCommand(CharacterPlugin plugin) {
        super(plugin, "view-permission", "characters.command.view-permission");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterViewPermissionCommand(plugin);
        return command.create()
                .then(command.remove())
                .then(command.set());
    }

    private ArgumentBuilder<CommandSourceStack, ?> remove() {
        return Commands.literal("remove").then(characterArgument(plugin)
                .executes(this::remove));
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set").then(characterArgument(plugin).then(permissionArgument()
                .executes(this::set)));
    }

    private RequiredArgumentBuilder<CommandSourceStack, String> permissionArgument() {
        return Commands.argument("permission", StringArgumentType.string());
    }

    private int remove(CommandContext<CommandSourceStack> context) {
        var success = set(context, null);
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private int set(CommandContext<CommandSourceStack> context) {
        var permission = context.getArgument("permission", String.class);
        var success = set(context, permission);
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private boolean set(CommandContext<CommandSourceStack> context, @Nullable String viewPermission) {
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
