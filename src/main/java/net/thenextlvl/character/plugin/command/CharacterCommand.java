package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterCommand {
    public static LiteralCommandNode<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("character")
                .then(CharacterActionCommand.create(plugin))
                .then(CharacterAttributeCommand.create(plugin))
                .then(CharacterCreateCommand.create(plugin))
                .then(CharacterDeleteCommand.create(plugin))
                .then(CharacterEquipmentCommand.create(plugin))
                .then(CharacterListCommand.create(plugin))
                .then(CharacterPoseCommand.create(plugin))
                .then(CharacterRenameCommand.create(plugin))
                .then(CharacterSkinCommand.create(plugin))
                .then(CharacterTagCommand.create(plugin))
                .then(CharacterTeleportCommand.create(plugin))
                .build();
    }
}
