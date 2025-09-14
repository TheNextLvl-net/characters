package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.CharacterArgument;
import net.thenextlvl.character.plugin.command.argument.PlayerCharacterArgument;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
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

    static RequiredArgumentBuilder<CommandSourceStack, ?> characterArgument(CharacterPlugin plugin) {
        return Commands.argument("character", new CharacterArgument(plugin));
    }

    static RequiredArgumentBuilder<CommandSourceStack, ?> playerCharacterArgument(CharacterPlugin plugin) {
        return Commands.argument("character", new PlayerCharacterArgument(plugin));
    }

    static RequiredArgumentBuilder<CommandSourceStack, ?> nameArgument(CharacterPlugin plugin) {
        return Commands.argument("name", StringArgumentType.word());
    }
}
