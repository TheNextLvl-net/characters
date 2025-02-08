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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.ColorArgument;
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterTagCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("tag")
                .then(reset(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(CharacterPlugin plugin) {
        return Commands.literal("reset").then(characterArgument(plugin)
                .then(resetAlignment(plugin))
                .then(resetBackgroundColor(plugin))
                .then(resetBillboard(plugin))
                .then(resetBrightness(plugin))
                .then(resetDefaultBackground(plugin))
                .then(resetLineWidth(plugin))
                .then(resetScale(plugin))
                .then(resetSeeThrough(plugin))
                .then(resetText(plugin))
                .then(resetTextOpacity(plugin))
                .then(resetTextShadow(plugin))
                .then(resetVisibility(plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetAlignment(CharacterPlugin plugin) {
        return Commands.literal("alignment").executes(context ->
                setAlignment(context, TextAlignment.CENTER, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetBackgroundColor(CharacterPlugin plugin) {
        return Commands.literal("background-color").executes(context ->
                setBackgroundColor(context, null, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetBillboard(CharacterPlugin plugin) {
        return Commands.literal("billboard").executes(context ->
                setBillboard(context, Billboard.CENTER, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetBrightness(CharacterPlugin plugin) {
        return Commands.literal("brightness").executes(context ->
                setBrightness(context, null, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetDefaultBackground(CharacterPlugin plugin) {
        return Commands.literal("default-background").executes(context ->
                setDefaultBackground(context, false, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetLineWidth(CharacterPlugin plugin) {
        return Commands.literal("line-width").executes(context ->
                setLineWidth(context, 200, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetScale(CharacterPlugin plugin) {
        return Commands.literal("scale").executes(context ->
                setScale(context, 1, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetSeeThrough(CharacterPlugin plugin) {
        return Commands.literal("see-through").executes(context ->
                setSeeThrough(context, false, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetText(CharacterPlugin plugin) {
        return Commands.literal("text").executes(context ->
                setText(context, null, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetTextOpacity(CharacterPlugin plugin) {
        return Commands.literal("text-opacity").executes(context ->
                setTextOpacity(context, 0, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetTextShadow(CharacterPlugin plugin) {
        return Commands.literal("text-shadow").executes(context ->
                setTextShadow(context, false, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetVisibility(CharacterPlugin plugin) {
        return Commands.literal("visibility").executes(context ->
                setVisible(context, true, plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(setAlignment(plugin))
                .then(setBackgroundColor(plugin))
                .then(setBillboard(plugin))
                .then(setBrightness(plugin))
                .then(setDefaultBackground(plugin))
                .then(setLineWidth(plugin))
                .then(setScale(plugin))
                .then(setSeeThrough(plugin))
                .then(setText(plugin))
                .then(setTextOpacity(plugin))
                .then(setTextShadow(plugin))
                .then(setVisible(plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setAlignment(CharacterPlugin plugin) {
        return Commands.literal("alignment").then(Commands.argument(
                "alignment", new EnumArgument<>(TextAlignment.class)
        ).executes(context -> {
            var alignment = context.getArgument("alignment", TextAlignment.class);
            return setAlignment(context, alignment, plugin);
        }));
    }

    private static int setAlignment(CommandContext<CommandSourceStack> context, TextAlignment alignment, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setAlignment(alignment);
        var message = success ? "character.tag.alignment" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", alignment.name().toLowerCase()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setBackgroundColor(CharacterPlugin plugin) {
        return Commands.literal("background-color").then(Commands.argument(
                "color", new ColorArgument()
        ).executes(context -> {
            var color = context.getArgument("color", Color.class);
            return setBackgroundColor(context, color, plugin);
        }));
    }

    private static int setBackgroundColor(CommandContext<CommandSourceStack> context, @Nullable Color color, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setBackgroundColor(color);
        var message = success ? "character.tag.background-color" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", color != null ? "#" + Integer.toHexString(color.asARGB()) : "null"));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setBillboard(CharacterPlugin plugin) {
        return Commands.literal("billboard").then(Commands.argument(
                "billboard", new EnumArgument<>(Billboard.class)
        ).executes(context -> {
            var billboard = context.getArgument("billboard", Billboard.class);
            return setBillboard(context, billboard, plugin);
        }));
    }

    private static int setBillboard(CommandContext<CommandSourceStack> context, Billboard billboard, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setBillboard(billboard);
        var message = success ? "character.tag.billboard" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", billboard.name().toLowerCase()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setBrightness(CharacterPlugin plugin) {
        return Commands.literal("brightness").then(Commands.argument(
                "block-light", IntegerArgumentType.integer(0, 15)
        ).then(Commands.argument(
                "sky-light", IntegerArgumentType.integer(0, 15)
        ).executes(context -> {
            var blockLight = context.getArgument("block-light", int.class);
            var skyLight = context.getArgument("sky-light", int.class);
            var brightness = new Brightness(blockLight, skyLight);
            return setBrightness(context, brightness, plugin);
        })));
    }

    private static int setBrightness(CommandContext<CommandSourceStack> context, @Nullable Brightness brightness, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setBrightness(brightness);
        var message = success ? "character.tag.brightness" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("block_light", String.valueOf(brightness != null ? brightness.getBlockLight() : null)),
                Placeholder.unparsed("sky_light", String.valueOf(brightness != null ? brightness.getSkyLight() : null)),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setDefaultBackground(CharacterPlugin plugin) {
        return Commands.literal("default-background").then(Commands.argument(
                "enabled", BoolArgumentType.bool()
        ).executes(context -> {
            var enabled = context.getArgument("enabled", boolean.class);
            return setDefaultBackground(context, enabled, plugin);
        }));
    }

    private static int setDefaultBackground(CommandContext<CommandSourceStack> context, boolean enabled, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setDefaultBackground(enabled);
        var message = success ? "character.tag.background" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(enabled)));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setLineWidth(CharacterPlugin plugin) {
        return Commands.literal("line-width").then(Commands.argument(
                "width", IntegerArgumentType.integer(0)
        ).executes(context -> {
            var width = context.getArgument("width", int.class);
            return setLineWidth(context, width, plugin);
        }));
    }

    private static int setLineWidth(CommandContext<CommandSourceStack> context, int width, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setLineWidth(width);
        var message = success ? "character.tag.line-width" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(width)));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setText(CharacterPlugin plugin) {
        return Commands.literal("text").then(Commands.argument(
                "text", StringArgumentType.greedyString()
        ).executes(context -> {
            var text = context.getArgument("text", String.class);
            var displayName = MiniMessage.miniMessage().deserialize(text);
            return setText(context, displayName, plugin);
        }));
    }

    private static int setText(CommandContext<CommandSourceStack> context, @Nullable Component text, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var success = character.setDisplayName(text);
        var message = success ? "character.tag.text" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.component("text", text != null ? text : Component.text(character.getName())),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setTextOpacity(CharacterPlugin plugin) {
        return Commands.literal("text-opacity").then(Commands.argument(
                "opacity", FloatArgumentType.floatArg(0, 100)
        ).executes(context -> {
            var opacity = context.getArgument("opacity", float.class);
            return setTextOpacity(context, opacity, plugin);
        }));
    }

    private static int setTextOpacity(CommandContext<CommandSourceStack> context, float opacity, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setTextOpacity(opacity);
        var message = success ? "character.tag.text-opacity" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Formatter.number("value", opacity),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setScale(CharacterPlugin plugin) {
        return Commands.literal("scale").then(Commands.argument(
                "scale", FloatArgumentType.floatArg(0.05f, 10)
        ).executes(context -> {
            var scale = context.getArgument("scale", float.class);
            return setScale(context, scale, plugin);
        }));
    }

    private static int setScale(CommandContext<CommandSourceStack> context, float scale, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setScale(scale);
        var message = success ? "character.tag.scale" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Formatter.number("value", scale),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setSeeThrough(CharacterPlugin plugin) {
        return Commands.literal("see-through").then(Commands.argument(
                "see-through", BoolArgumentType.bool()
        ).executes(context -> {
            var seeThrough = context.getArgument("see-through", boolean.class);
            return setSeeThrough(context, seeThrough, plugin);
        }));
    }

    private static int setSeeThrough(CommandContext<CommandSourceStack> context, boolean seeThrough, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setSeeThrough(seeThrough);
        var message = !success ? "nothing.changed" : seeThrough
                ? "character.tag.see-through" : "character.tag.not-see-through";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setTextShadow(CharacterPlugin plugin) {
        return Commands.literal("text-shadow").then(Commands.argument(
                "enabled", BoolArgumentType.bool()
        ).executes(context -> {
            var enabled = context.getArgument("enabled", boolean.class);
            return setTextShadow(context, enabled, plugin);
        }));
    }

    private static int setTextShadow(CommandContext<CommandSourceStack> context, boolean enabled, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setTextShadow(enabled);
        var message = success ? "character.tag.text-shadow" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(enabled)));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setVisible(CharacterPlugin plugin) {
        return Commands.literal("visible").then(Commands.argument(
                "visible", BoolArgumentType.bool()
        ).executes(context -> {
            var visible = context.getArgument("visible", boolean.class);
            return setVisible(context, visible, plugin);
        }));
    }

    private static int setVisible(CommandContext<CommandSourceStack> context, boolean visible, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var success = character.setDisplayNameVisible(visible);
        var message = !success ? "nothing.changed" : visible ? "character.tag.visible" : "character.tag.invisible";

        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
