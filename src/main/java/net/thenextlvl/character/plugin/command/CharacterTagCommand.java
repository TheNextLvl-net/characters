package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.ColorArgument;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

// todo: split up into multiple commands
@NullMarked
final class CharacterTagCommand extends BrigadierCommand {
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
                .then(resetAlignment())
                .then(resetBackgroundColor())
                .then(resetBillboard())
                .then(resetBrightness())
                .then(resetDefaultBackground())
                .then(resetLeftRotation())
                .then(resetLineWidth())
                .then(resetOffset())
                .then(resetRightRotation())
                .then(resetScale())
                .then(resetSeeThrough())
                .then(resetText())
                .then(resetTextOpacity())
                .then(resetTextShadow())
                .then(resetVisibility()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetAlignment() {
        return Commands.literal("alignment").executes(context ->
                setAlignment(context, TextAlignment.CENTER));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetBackgroundColor() {
        return Commands.literal("background-color").executes(context ->
                setBackgroundColor(context, null));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetBillboard() {
        return Commands.literal("billboard").executes(context ->
                setBillboard(context, Billboard.CENTER));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetBrightness() {
        return Commands.literal("brightness").executes(context ->
                setBrightness(context, null));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetDefaultBackground() {
        return Commands.literal("default-background").executes(context ->
                setDefaultBackground(context, false));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetLeftRotation() {
        return Commands.literal("left-rotation").executes(context ->
                setRotation(context, new Quaternionf(), true));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetLineWidth() {
        return Commands.literal("line-width").executes(context ->
                setLineWidth(context, 200));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetOffset() {
        return Commands.literal("offset").executes(context ->
                setOffset(context, new Vector3f(0, 0.27f, 0)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetRightRotation() {
        return Commands.literal("right-rotation").executes(context ->
                setRotation(context, new Quaternionf(), false));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetScale() {
        return Commands.literal("scale").executes(context ->
                setScale(context, 1));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetSeeThrough() {
        return Commands.literal("see-through").executes(context ->
                setSeeThrough(context, false));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetText() {
        return Commands.literal("text").executes(context ->
                setText(context, null));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetTextOpacity() {
        return Commands.literal("text-opacity").executes(context ->
                setTextOpacity(context, 0));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetTextShadow() {
        return Commands.literal("text-shadow").executes(context ->
                setTextShadow(context, false));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetVisibility() {
        return Commands.literal("visibility").executes(context ->
                setVisible(context, true));
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(setAlignment())
                .then(setBackgroundColor())
                .then(setBillboard())
                .then(setBrightness())
                .then(setDefaultBackground())
                .then(setLineWidth())
                .then(setOffset())
                .then(setRotation(false))
                .then(setRotation(true))
                .then(setScale())
                .then(setSeeThrough())
                .then(setText())
                .then(setTextOpacity())
                .then(setTextShadow())
                .then(setVisible()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> setAlignment() {
        return Commands.literal("alignment").then(Commands.argument(
                "alignment", EnumArgumentType.of(TextAlignment.class, EnumStringCodec.lowerHyphen())
        ).executes(context -> {
            var alignment = context.getArgument("alignment", TextAlignment.class);
            return setAlignment(context, alignment);
        }));
    }

    private int setAlignment(CommandContext<CommandSourceStack> context, TextAlignment alignment) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setAlignment(alignment);
        var message = success ? "character.tag.alignment" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", alignment.name().toLowerCase()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setBackgroundColor() {
        return Commands.literal("background-color").then(Commands.argument(
                "color", new ColorArgument()
        ).executes(context -> {
            var color = context.getArgument("color", Color.class);
            return setBackgroundColor(context, color);
        }));
    }

    private int setBackgroundColor(CommandContext<CommandSourceStack> context, @Nullable Color color) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setBackgroundColor(color);
        var message = success ? "character.tag.background-color" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", color != null ? "#" + Integer.toHexString(color.asARGB()) : "null"));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setBillboard() {
        return Commands.literal("billboard").then(Commands.argument(
                "billboard", EnumArgumentType.of(Billboard.class, EnumStringCodec.lowerHyphen())
        ).executes(context -> {
            var billboard = context.getArgument("billboard", Billboard.class);
            return setBillboard(context, billboard);
        }));
    }

    private int setBillboard(CommandContext<CommandSourceStack> context, Billboard billboard) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setBillboard(billboard);
        var message = success ? "character.tag.billboard" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", billboard.name().toLowerCase()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setBrightness() {
        return Commands.literal("brightness").then(Commands.argument(
                "block-light", IntegerArgumentType.integer(0, 15)
        ).then(Commands.argument(
                "sky-light", IntegerArgumentType.integer(0, 15)
        ).executes(context -> {
            var blockLight = context.getArgument("block-light", int.class);
            var skyLight = context.getArgument("sky-light", int.class);
            var brightness = new Brightness(blockLight, skyLight);
            return setBrightness(context, brightness);
        })));
    }

    private int setBrightness(CommandContext<CommandSourceStack> context, @Nullable Brightness brightness) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setBrightness(brightness);
        var message = success ? "character.tag.brightness" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("block_light", String.valueOf(brightness != null ? brightness.getBlockLight() : null)),
                Placeholder.unparsed("sky_light", String.valueOf(brightness != null ? brightness.getSkyLight() : null)),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setDefaultBackground() {
        return Commands.literal("default-background").then(Commands.argument(
                "enabled", BoolArgumentType.bool()
        ).executes(context -> {
            var enabled = context.getArgument("enabled", boolean.class);
            return setDefaultBackground(context, enabled);
        }));
    }

    private int setDefaultBackground(CommandContext<CommandSourceStack> context, boolean enabled) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setDefaultBackground(enabled);
        var message = success ? "character.tag.background" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(enabled)));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setLineWidth() {
        return Commands.literal("line-width").then(Commands.argument(
                "width", IntegerArgumentType.integer(0)
        ).executes(context -> {
            var width = context.getArgument("width", int.class);
            return setLineWidth(context, width);
        }));
    }

    private int setLineWidth(CommandContext<CommandSourceStack> context, int width) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setLineWidth(width);
        var message = success ? "character.tag.line-width" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(width)));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setOffset() {
        return Commands.literal("offset").then(Commands.argument(
                "x", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "y", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "z", FloatArgumentType.floatArg()
        ).executes(context -> {
            var x = context.getArgument("x", float.class);
            var y = context.getArgument("y", float.class);
            var z = context.getArgument("z", float.class);
            return setOffset(context, new Vector3f(x, y, z));
        }))));
    }

    private int setOffset(CommandContext<CommandSourceStack> context, Vector3f offset) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setOffset(offset);
        var message = success ? "character.tag.offset" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Formatter.number("x", offset.x()),
                Formatter.number("y", offset.y()),
                Formatter.number("z", offset.z()),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setRotation(boolean left) {
        return Commands.literal((left ? "left" : "right") + "-rotation").then(Commands.argument(
                "x", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "y", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "z", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "w", FloatArgumentType.floatArg()
        ).executes(context -> {
            var w = context.getArgument("w", float.class);
            var x = context.getArgument("x", float.class);
            var y = context.getArgument("y", float.class);
            var z = context.getArgument("z", float.class);
            return setRotation(context, new Quaternionf(x, y, z, w), left);
        })))));
    }

    private int setRotation(CommandContext<CommandSourceStack> context, Quaternionf rotation, boolean left) {
        var character = context.getArgument("character", Character.class);
        var success = left ? character.getTagOptions().setLeftRotation(rotation)
                : character.getTagOptions().setRightRotation(rotation);
        var message = !success ? "nothing.changed" : left
                ? "character.tag.left-rotation" : "character.tag.right-rotation";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Formatter.number("w", rotation.w()),
                Formatter.number("x", rotation.x()),
                Formatter.number("y", rotation.y()),
                Formatter.number("z", rotation.z()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setText() {
        return Commands.literal("text").then(Commands.argument(
                "text", StringArgumentType.greedyString()
        ).executes(context -> {
            var text = context.getArgument("text", String.class);
            var displayName = MiniMessage.miniMessage().deserialize(text);
            return setText(context, displayName);
        }));
    }

    private int setText(CommandContext<CommandSourceStack> context, @Nullable Component text) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var success = character.setDisplayName(text);
        var message = success ? "character.tag.text" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.component("text", text != null ? text : Component.text(character.getName())),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setTextOpacity() {
        return Commands.literal("text-opacity").then(Commands.argument(
                "opacity", FloatArgumentType.floatArg(0, 100)
        ).executes(context -> {
            var opacity = context.getArgument("opacity", float.class);
            return setTextOpacity(context, opacity);
        }));
    }

    private int setTextOpacity(CommandContext<CommandSourceStack> context, float opacity) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setTextOpacity(opacity);
        var message = success ? "character.tag.text-opacity" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Formatter.number("value", opacity),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setScale() {
        return Commands.literal("scale").then(Commands.argument(
                "scale", FloatArgumentType.floatArg(0.05f, 10)
        ).executes(context -> {
            var scale = context.getArgument("scale", float.class);
            return setScale(context, scale);
        }));
    }

    private int setScale(CommandContext<CommandSourceStack> context, float scale) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setScale(scale);
        var message = success ? "character.tag.scale" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Formatter.number("value", scale),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setSeeThrough() {
        return Commands.literal("see-through").then(Commands.argument(
                "see-through", BoolArgumentType.bool()
        ).executes(context -> {
            var seeThrough = context.getArgument("see-through", boolean.class);
            return setSeeThrough(context, seeThrough);
        }));
    }

    private int setSeeThrough(CommandContext<CommandSourceStack> context, boolean seeThrough) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setSeeThrough(seeThrough);
        var message = !success ? "nothing.changed" : seeThrough
                ? "character.tag.see-through" : "character.tag.not-see-through";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setTextShadow() {
        return Commands.literal("text-shadow").then(Commands.argument(
                "enabled", BoolArgumentType.bool()
        ).executes(context -> {
            var enabled = context.getArgument("enabled", boolean.class);
            return setTextShadow(context, enabled);
        }));
    }

    private int setTextShadow(CommandContext<CommandSourceStack> context, boolean enabled) {
        var character = context.getArgument("character", Character.class);
        var success = character.getTagOptions().setTextShadow(enabled);
        var message = success ? "character.tag.text-shadow" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(enabled)));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setVisible() {
        return Commands.literal("visible").then(Commands.argument(
                "visible", BoolArgumentType.bool()
        ).executes(context -> {
            var visible = context.getArgument("visible", boolean.class);
            return setVisible(context, visible);
        }));
    }

    private int setVisible(CommandContext<CommandSourceStack> context, boolean visible) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var success = character.setDisplayNameVisible(visible);
        var message = !success ? "nothing.changed" : visible ? "character.tag.visible" : "character.tag.invisible";

        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
