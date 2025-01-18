package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterTagCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("tag")
                .then(hide(plugin))
                .then(reset(plugin))
                .then(set(plugin))
                .then(show(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> tagArgument(CharacterPlugin plugin) {
        return Commands.argument("tag", StringArgumentType.greedyString());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> hide(CharacterPlugin plugin) {
        return Commands.literal("hide").then(characterArgument(plugin)
                .executes(context -> toggle(context, plugin, false)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(CharacterPlugin plugin) {
        return Commands.literal("reset").then(characterArgument(plugin)
                .executes(context -> reset(context, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(tagArgument(plugin).executes(context -> set(context, plugin))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> show(CharacterPlugin plugin) {
        return Commands.literal("show").then(characterArgument(plugin)
                .executes(context -> toggle(context, plugin, true)));
    }

    private static int toggle(CommandContext<CommandSourceStack> context, CharacterPlugin plugin, boolean visible) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var success = character.setDisplayNameVisible(visible);
        var message = !success ? "nothing.changed" : visible ? "character.tag.shown" : "character.tag.hidden";

        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static int reset(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var success = character.setDisplayName(null);
        var message = success ? "character.tag.reset" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static int set(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);
        var tag = context.getArgument("tag", String.class);
        var displayName = MiniMessage.miniMessage().deserialize(tag);

        var success = character.setDisplayName(displayName);
        var message = success ? "character.tag.set" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.component("tag", displayName));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
