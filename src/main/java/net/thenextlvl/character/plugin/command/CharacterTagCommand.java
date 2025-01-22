package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.thenextlvl.character.plugin.command.argument.ColorArgument;
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterTagCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("tag")
                .then(reset(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(CharacterPlugin plugin) {
        return Commands.literal("reset");
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(setAlignment(plugin))
                .then(setBackgroundColor(plugin))
                .then(setBillboard(plugin))
                .then(setBrightness(plugin))
                .then(setDefaultBackground(plugin))
                .then(setDisplayHeight(plugin))
                .then(setDisplayWidth(plugin))
                .then(setTextOpacity(plugin))
                .then(setScale(plugin))
                .then(setSeeThrough(plugin))
                .then(setTextShadow(plugin))
                .then(setShadowRadius(plugin))
                .then(setShadowStrength(plugin))
                .then(setText(plugin))
                .then(setVisible(plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setAlignment(CharacterPlugin plugin) {
        return Commands.literal("alignment").then(Commands.argument(
                "alignment", new EnumArgument<>(TextAlignment.class)
        ).executes(context -> {
            var alignment = context.getArgument("alignment", TextAlignment.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setAlignment(alignment);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setBackgroundColor(CharacterPlugin plugin) {
        return Commands.literal("background-color").then(Commands.argument(
                "color", new ColorArgument()
        ).executes(context -> {
            var color = context.getArgument("color", Color.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setBackgroundColor(color);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setText(CharacterPlugin plugin) {
        return Commands.literal("text").then(Commands.argument(
                "text", StringArgumentType.greedyString()
        ).executes(context -> setText(context, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setBillboard(CharacterPlugin plugin) {
        return Commands.literal("billboard").then(Commands.argument(
                "billboard", new EnumArgument<>(Billboard.class)
        ).executes(context -> {
            var billboard = context.getArgument("billboard", Billboard.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setBillboard(billboard);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setBrightness(CharacterPlugin plugin) {
        return Commands.literal("brightness").then(Commands.argument(
                "block-light", IntegerArgumentType.integer(0, 15)
        ).then(Commands.argument(
                "sky-light", IntegerArgumentType.integer(0, 15)
        ).executes(context -> {
            var blockLight = context.getArgument("block-light", int.class);
            var skyLight = context.getArgument("sky-light", int.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setBrightness(new Brightness(blockLight, skyLight));
            return success ? Command.SINGLE_SUCCESS : 0;
        })));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setDefaultBackground(CharacterPlugin plugin) {
        return Commands.literal("default-background").then(Commands.argument(
                "enabled", BoolArgumentType.bool()
        ).executes(context -> {
            var enabled = context.getArgument("enabled", boolean.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setDefaultBackground(enabled);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setDisplayHeight(CharacterPlugin plugin) {
        return Commands.literal("display-height").then(Commands.argument(
                "height", FloatArgumentType.floatArg()
        ).executes(context -> {
            var height = context.getArgument("height", float.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setDisplayHeight(height);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setDisplayWidth(CharacterPlugin plugin) {
        return Commands.literal("display-width").then(Commands.argument(
                "width", FloatArgumentType.floatArg()
        ).executes(context -> {
            var width = context.getArgument("width", float.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setDisplayWidth(width);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setTextOpacity(CharacterPlugin plugin) {
        return Commands.literal("text-opacity").then(Commands.argument(
                "percentage", FloatArgumentType.floatArg(0, 100)
        ).executes(context -> {
            var opacity = context.getArgument("percentage", float.class);
            var alpha = Math.round(25 + ((100 - opacity) * 2.3));
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setTextOpacity((byte) alpha);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setScale(CharacterPlugin plugin) {
        return Commands.literal("scale").then(Commands.argument(
                "scale", FloatArgumentType.floatArg(0.05f, 10)
        ).executes(context -> {
            var scale = context.getArgument("scale", float.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setScale(new Vector3f(scale));
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setSeeThrough(CharacterPlugin plugin) {
        return Commands.literal("see-through").then(Commands.argument(
                "see-through", BoolArgumentType.bool()
        ).executes(context -> {
            var seeThrough = context.getArgument("see-through", boolean.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setSeeThrough(seeThrough);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setTextShadow(CharacterPlugin plugin) {
        return Commands.literal("text-shadow").then(Commands.argument(
                "enabled", BoolArgumentType.bool()
        ).executes(context -> {
            var enabled = context.getArgument("enabled", boolean.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setTextShadow(enabled);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setShadowRadius(CharacterPlugin plugin) {
        return Commands.literal("shadow-radius").then(Commands.argument(
                "radius", FloatArgumentType.floatArg(0)
        ).executes(context -> {
            var radius = context.getArgument("radius", float.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setShadowRadius(radius);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setShadowStrength(CharacterPlugin plugin) {
        return Commands.literal("shadow-strength").then(Commands.argument(
                "strength", FloatArgumentType.floatArg(0)
        ).executes(context -> {
            var strength = context.getArgument("strength", float.class);
            var character = context.getArgument("character", Character.class);
            var success = character.getTagOptions().setShadowStrength(strength);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setVisible(CharacterPlugin plugin) {
        return Commands.literal("visible").then(Commands.argument(
                "visible", BoolArgumentType.bool()
        ).executes(context -> setVisible(context, plugin)));
    }

    private static int setVisible(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var visible = context.getArgument("visible", boolean.class);
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

    private static int setText(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);
        var text = context.getArgument("text", String.class);
        var displayName = MiniMessage.miniMessage().deserialize(text);

        var success = character.setDisplayName(displayName);
        var message = success ? "character.tag.set" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.component("text", displayName));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
