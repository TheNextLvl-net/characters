package net.thenextlvl.character.plugin.command.tag;

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
import net.thenextlvl.character.plugin.command.argument.ColorArgumentType;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

// todo: split up into multiple commands
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
        return Commands.literal("alignment").executes(this::setAlignment);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetBackgroundColor() {
        return Commands.literal("background-color").executes(this::setBackgroundColor);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetBillboard() {
        return Commands.literal("billboard").executes(this::setBillboard);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetBrightness() {
        return Commands.literal("brightness").executes(this::setBrightness);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetDefaultBackground() {
        return Commands.literal("default-background").executes(this::setDefaultBackground);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetLeftRotation() {
        return Commands.literal("left-rotation").executes(context -> setRotation(context, true));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetLineWidth() {
        return Commands.literal("line-width").executes(this::setLineWidth);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetOffset() {
        return Commands.literal("offset").executes(this::setOffset);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetRightRotation() {
        return Commands.literal("right-rotation").executes(context -> setRotation(context, false));
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetScale() {
        return Commands.literal("scale").executes(this::setScale);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetSeeThrough() {
        return Commands.literal("see-through").executes(this::setSeeThrough);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetText() {
        return Commands.literal("text").executes(this::setText);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetTextOpacity() {
        return Commands.literal("text-opacity").executes(this::setTextOpacity);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetTextShadow() {
        return Commands.literal("text-shadow").executes(this::setTextShadow);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetVisibility() {
        return Commands.literal("visibility").executes(this::setVisible);
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
        ).executes(this::setAlignment));
    }

    private int setAlignment(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var alignment = tryGetArgument(context, "alignment", TextAlignment.class).orElse(TextAlignment.CENTER);
        var success = character.getTagOptions().setAlignment(alignment);
        var message = success ? "character.tag.alignment" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", alignment.name().toLowerCase()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setBackgroundColor() {
        return Commands.literal("background-color").then(Commands.argument(
                "color", new ColorArgumentType()
        ).executes(this::setBackgroundColor));
    }

    private int setBackgroundColor(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var color = tryGetArgument(context, "color", Color.class).orElse(null);
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
        ).executes(this::setBillboard));
    }

    private int setBillboard(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var billboard = tryGetArgument(context, "billboard", Billboard.class).orElse(Billboard.CENTER);
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
        ).executes(this::setBrightness)));
    }

    private int setBrightness(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var blockLight = tryGetArgument(context, "block-light", int.class).orElse(null);
        var skyLight = tryGetArgument(context, "sky-light", int.class).orElse(null);
        var brightness = blockLight != null && skyLight != null ? new Brightness(blockLight, skyLight) : null;
        var success = character.getTagOptions().setBrightness(brightness);
        var message = success ? "character.tag.brightness" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("block_light", String.valueOf(blockLight)),
                Placeholder.unparsed("sky_light", String.valueOf(skyLight)),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setDefaultBackground() {
        return Commands.literal("default-background").then(Commands.argument(
                "enabled", BoolArgumentType.bool()
        ).executes(this::setDefaultBackground));
    }

    private int setDefaultBackground(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var enabled = tryGetArgument(context, "enabled", boolean.class).orElse(false);
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
        ).executes(this::setLineWidth));
    }

    private int setLineWidth(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var width = tryGetArgument(context, "width", int.class).orElse(200);
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
        ).executes(this::setOffset))));
    }

    private int setOffset(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var x = tryGetArgument(context, "x", float.class).orElse(0.0f);
        var y = tryGetArgument(context, "y", float.class).orElse(0.27f);
        var z = tryGetArgument(context, "z", float.class).orElse(0.0f);
        var success = character.getTagOptions().setOffset(new Vector3f(x, y, z));
        var message = success ? "character.tag.offset" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Formatter.number("x", x),
                Formatter.number("y", y),
                Formatter.number("z", z),
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
        ).executes(context -> setRotation(context, left))))));
    }

    private int setRotation(CommandContext<CommandSourceStack> context, boolean left) {
        var character = context.getArgument("character", Character.class);
        var w = tryGetArgument(context, "w", float.class).orElse(1.0f);
        var x = tryGetArgument(context, "x", float.class).orElse(0.0f);
        var y = tryGetArgument(context, "y", float.class).orElse(0.0f);
        var z = tryGetArgument(context, "z", float.class).orElse(0.0f);
        var rotation = new Quaternionf(x, y, z, w);
        var success = left ? character.getTagOptions().setLeftRotation(rotation)
                : character.getTagOptions().setRightRotation(rotation);
        var message = !success ? "nothing.changed" : left
                ? "character.tag.left-rotation" : "character.tag.right-rotation";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Formatter.number("w", w),
                Formatter.number("x", x),
                Formatter.number("y", y),
                Formatter.number("z", z));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> setText() {
        return Commands.literal("text").then(Commands.argument(
                "text", StringArgumentType.greedyString()
        ).executes(this::setText));
    }

    private int setText(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);
        var text = tryGetArgument(context, "text", String.class).map(MiniMessage.miniMessage()::deserialize).orElse(null);

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
        ).executes(this::setTextOpacity));
    }

    private int setTextOpacity(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var opacity = tryGetArgument(context, "opacity", float.class).orElse(0f);
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
        ).executes(this::setScale));
    }

    private int setScale(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var scale = tryGetArgument(context, "scale", float.class).orElse(1.0f);
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
        ).executes(this::setSeeThrough));
    }

    private int setSeeThrough(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var seeThrough = tryGetArgument(context, "see-through", boolean.class).orElse(false);
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
        ).executes(this::setTextShadow));
    }

    private int setTextShadow(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var enabled = tryGetArgument(context, "enabled", boolean.class).orElse(false);
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
        ).executes(this::setVisible));
    }

    private int setVisible(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);
        var visible = tryGetArgument(context, "visible", boolean.class).orElse(true);

        var success = character.setDisplayNameVisible(visible);
        var message = !success ? "nothing.changed" : visible ? "character.tag.visible" : "character.tag.invisible";

        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
