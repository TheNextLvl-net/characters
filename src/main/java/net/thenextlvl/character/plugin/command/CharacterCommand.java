package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.suggestion.CharacterSuggestionProvider;
import net.thenextlvl.character.plugin.command.suggestion.PlayerCharacterSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterCommand {
    public static LiteralCommandNode<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("character")
                // todo: add permissions
                .then(CharacterActionCommand.create(plugin))
                .then(CharacterAttributeCommand.create(plugin))
                .then(CharacterCreateCommand.create(plugin))
                .then(CharacterDeleteCommand.create(plugin))
                .then(CharacterEquipmentCommand.create(plugin))
                .then(CharacterListCommand.create(plugin))
                .then(CharacterPoseCommand.create(plugin))
                .then(CharacterSkinCommand.create(plugin))
                .then(CharacterTagCommand.create(plugin))
                .then(CharacterTeleportCommand.create(plugin))
                .build();
    }

    static RequiredArgumentBuilder<CommandSourceStack, ?> characterArgument(CharacterPlugin plugin) {
        return Commands.argument("character", StringArgumentType.word())
                .suggests(new CharacterSuggestionProvider(plugin));
    }

    static RequiredArgumentBuilder<CommandSourceStack, ?> playerCharacterArgument(CharacterPlugin plugin) {
        return Commands.argument("character", StringArgumentType.word())
                .suggests(new PlayerCharacterSuggestionProvider(plugin));
    }

    static RequiredArgumentBuilder<CommandSourceStack, ?> nameArgument(CharacterPlugin plugin) {
        return Commands.argument("name", StringArgumentType.word());
    }
}
