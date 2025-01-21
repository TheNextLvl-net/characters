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
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterTagCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("tag")
                .then(reset(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> alignmentArgument(CharacterPlugin plugin) {
        return Commands.argument("alignment", new EnumArgument<>(TextDisplay.TextAlignment.class));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> billboardArgument(CharacterPlugin plugin) {
        return Commands.argument("billboard", new EnumArgument<>(Display.Billboard.class));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> textArgument(CharacterPlugin plugin) {
        return Commands.argument("text", StringArgumentType.greedyString());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(CharacterPlugin plugin) {
        return Commands.literal("reset");
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(setAlignment(plugin))
                // .then(set("background-color", Color.class, , plugin))
                .then(setBillboard(plugin))
                // .then(set("brightness", Display.Brightness.class, , plugin))
                // .then(set("default-background", boolean.class, BoolArgumentType.bool(), plugin))
                // .then(set("display-height", float.class, FloatArgumentType.floatArg(), plugin))
                // .then(set("display-width", float.class, FloatArgumentType.floatArg(), plugin))
                // .then(set("line-width", int.class, IntegerArgumentType.integer(), plugin))
                // .then(set("text-opacity", byte.class, IntegerArgumentType.integer(Byte.MIN_VALUE, Byte.MAX_VALUE), plugin))
                // .then(set("scale", Vector3f.class, FloatArgumentType.floatArg(), plugin))
                // .then(set("see-through", boolean.class, BoolArgumentType.bool(), plugin))
                // .then(set("shadow-radius", float.class, FloatArgumentType.floatArg(), plugin))
                // .then(set("shadow-strength", float.class, FloatArgumentType.floatArg(), plugin))
                // .then(set("shadowed", boolean.class, BoolArgumentType.bool(), plugin))
                .then(setText(plugin))
        );
                // .then(set("visible", boolean.class, BoolArgumentType.bool(), plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setAlignment(CharacterPlugin plugin) {
        return Commands.literal("alignment").then(alignmentArgument(plugin).executes(context -> {
            var alignment = context.getArgument("alignment", TextDisplay.TextAlignment.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setAlignment(alignment);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setText(CharacterPlugin plugin) {
        return Commands.literal("text").then(textArgument(plugin).executes(context -> {
            var text = context.getArgument("text", String.class);
            var character = context.getArgument("character", Character.class);
            var success = character.setDisplayName(MiniMessage.miniMessage().deserialize(text));
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setBillboard(CharacterPlugin plugin) {
        return Commands.literal("billboard").then(billboardArgument(plugin).executes(context -> {
            var billboard = context.getArgument("billboard", Display.Billboard.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setBillboard(billboard);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static int setVisible(CommandContext<CommandSourceStack> context, CharacterPlugin plugin, boolean visible) {
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
