package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.action.CharacterActionCommand;
import net.thenextlvl.character.plugin.command.argument.CharacterArgumentType;
import net.thenextlvl.character.plugin.command.argument.PlayerCharacterArgumentType;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.plugin.command.suggestion.PermissionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CharacterCommand extends BrigadierCommand {
    private CharacterCommand(CharacterPlugin plugin) {
        super(plugin, "character", "characters.command");
    }

    public static LiteralCommandNode<CommandSourceStack> create(CharacterPlugin plugin) {
        return new CharacterCommand(plugin).create()
                .then(CharacterActionCommand.create(plugin))
                .then(CharacterAttributeCommand.create(plugin))
                .then(CharacterCreateCommand.create(plugin))
                .then(CharacterDeleteCommand.create(plugin))
                .then(CharacterEquipmentCommand.create(plugin))
                // .then(CharacterGoalCommand.create(plugin))
                .then(CharacterListCommand.create(plugin))
                .then(CharacterSaveCommand.create(plugin))
                .then(CharacterSkinCommand.create(plugin))
                .then(CharacterTagCommand.create(plugin))
                .then(CharacterTeleportCommand.create(plugin))
                .then(CharacterViewPermissionCommand.create(plugin))
                .build();
    }

    public static RequiredArgumentBuilder<CommandSourceStack, String> permissionArgument(CharacterPlugin plugin) {
        return Commands.argument("permission", StringArgumentType.string())
                .suggests(new PermissionSuggestionProvider<>(plugin));
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ?> characterArgument(CharacterPlugin plugin) {
        return Commands.argument("character", new CharacterArgumentType(plugin));
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ?> playerCharacterArgument(CharacterPlugin plugin) {
        return Commands.argument("character", new PlayerCharacterArgumentType(plugin));
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ?> nameArgument(CharacterPlugin plugin) {
        return Commands.argument("name", StringArgumentType.word());
    }
}
