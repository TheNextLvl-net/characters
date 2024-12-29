package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.PoseArgument;
import org.bukkit.entity.Pose;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterPoseCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("pose").then(characterArgument(plugin).then(poseArgument(plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> poseArgument(CharacterPlugin plugin) {
        return Commands.argument("pose", new PoseArgument())
                .executes(context -> pose(context, plugin));
    }

    private static int pose(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("character", String.class);
        var pose = context.getArgument("pose", Pose.class);
        var character = plugin.characterController().getCharacter(name).orElse(null);

        if (character == null) {
            plugin.bundle().sendMessage(sender, "character.not_found", Placeholder.unparsed("name", name));
            return 0;
        } else if (character.getPose().equals(pose)) {
            plugin.bundle().sendMessage(sender, "nothing.changed");
            return 0;
        }

        character.setPose(pose);
        plugin.bundle().sendMessage(sender, "character.pose",
                Placeholder.unparsed("character", name),
                Placeholder.unparsed("pose", pose.name().toLowerCase()));
        return Command.SINGLE_SUCCESS;
    }
}
