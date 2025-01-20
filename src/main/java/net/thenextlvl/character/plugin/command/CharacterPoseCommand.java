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
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import org.bukkit.entity.Pose;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterPoseCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("pose")
                .then(reset(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> poseArgument(CharacterPlugin plugin) {
        return Commands.argument("pose", new EnumArgument<>(Pose.class));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(CharacterPlugin plugin) {
        return Commands.literal("reset").then(characterArgument(plugin)
                .executes(context -> reset(context, plugin)));
    }

    private static int reset(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        return set(context, Pose.STANDING, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin).then(poseArgument(plugin)
                .executes(context -> set(context, plugin))));
    }

    private static int set(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var pose = context.getArgument("pose", Pose.class);
        return set(context, pose, plugin);
    }

    private static int set(CommandContext<CommandSourceStack> context, Pose pose, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var success = character.setPose(pose);
        var message = success ? "character.pose" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("pose", pose.name().toLowerCase()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
