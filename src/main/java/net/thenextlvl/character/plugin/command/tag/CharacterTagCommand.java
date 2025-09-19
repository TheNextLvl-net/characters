package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
public final class CharacterTagCommand extends BrigadierCommand {
    private CharacterTagCommand(CharacterPlugin plugin) {
        super(plugin, "tag", "characters.command.tag");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterTagCommand(plugin);
        return command.create()
                .then(command.reset())
                .then(command.set());
    }

    private ArgumentBuilder<CommandSourceStack, ?> reset() {
        return Commands.literal("reset").then(characterArgument(plugin)
                .then(CharacterTagAlignmentCommand.reset(plugin))
                .then(CharacterTagBackgroundColorCommand.reset(plugin))
                .then(CharacterTagBillboardCommand.reset(plugin))
                .then(CharacterTagBrightnessCommand.reset(plugin))
                .then(CharacterTagDefaultBackgroundCommand.reset(plugin))
                .then(CharacterTagLineWidthCommand.reset(plugin))
                .then(CharacterTagOffsetCommand.reset(plugin))
                .then(CharacterTagRotationCommand.reset(plugin, CharacterTagRotationCommand.Rotation.LEFT))
                .then(CharacterTagRotationCommand.reset(plugin, CharacterTagRotationCommand.Rotation.RIGHT))
                .then(CharacterTagScaleCommand.reset(plugin))
                .then(CharacterTagSeeThroughCommand.reset(plugin))
                .then(CharacterTagTextCommand.reset(plugin))
                .then(CharacterTagTextOpacityCommand.reset(plugin))
                .then(CharacterTagTextShadowCommand.reset(plugin))
                .then(CharacterTagVisibilityCommand.reset(plugin)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(CharacterTagAlignmentCommand.set(plugin))
                .then(CharacterTagBackgroundColorCommand.set(plugin))
                .then(CharacterTagBillboardCommand.set(plugin))
                .then(CharacterTagBrightnessCommand.set(plugin))
                .then(CharacterTagDefaultBackgroundCommand.set(plugin))
                .then(CharacterTagLineWidthCommand.set(plugin))
                .then(CharacterTagOffsetCommand.set(plugin))
                .then(CharacterTagRotationCommand.set(plugin, CharacterTagRotationCommand.Rotation.LEFT))
                .then(CharacterTagRotationCommand.set(plugin, CharacterTagRotationCommand.Rotation.RIGHT))
                .then(CharacterTagScaleCommand.set(plugin))
                .then(CharacterTagSeeThroughCommand.set(plugin))
                .then(CharacterTagTextCommand.set(plugin))
                .then(CharacterTagTextOpacityCommand.set(plugin))
                .then(CharacterTagTextShadowCommand.set(plugin))
                .then(CharacterTagVisibilityCommand.set(plugin)));
    }
}
