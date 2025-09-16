package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.ColorArgumentType;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

public final class CharacterTagCommand extends BrigadierCommand {
    private CharacterTagCommand(@NonNull CharacterPlugin plugin) {
        super(plugin, "tag", "characters.command.tag");
    }

    public static @NonNull LiteralArgumentBuilder<CommandSourceStack> create(@NonNull CharacterPlugin plugin) {
        var command = new CharacterTagCommand(plugin);
        // todo:
        //  brightness
        //  offset
        //  right rotation
        //  text
        //  left rotation
        return command.create().then(characterArgument(plugin).then(command.option(
                        "alignment", "alignment",
                        EnumArgumentType.of(TextAlignment.class, EnumStringCodec.lowerHyphen()),
                        TextAlignment.class,
                        (ch, v) -> ch.getTagOptions().setAlignment(v),
                        v -> "character.tag.alignment",
                        v -> List.of(Placeholder.unparsed("value", v.name().toLowerCase())),
                        TextAlignment.CENTER
                )).then(command.option(
                        "background-color", "color",
                        new ColorArgumentType(), Color.class,
                        (ch, v) -> ch.getTagOptions().setBackgroundColor(v),
                        v -> "character.tag.background-color",
                        v -> List.of(Placeholder.unparsed("value", v != null ? "#" + Integer.toHexString(v.asARGB()) : "null")),
                        null
                )).then(command.option(
                        "billboard", "billboard",
                        EnumArgumentType.of(Billboard.class, EnumStringCodec.lowerHyphen()),
                        Billboard.class,
                        (ch, v) -> ch.getTagOptions().setBillboard(v),
                        v -> "character.tag.billboard",
                        v -> List.of(Placeholder.unparsed("value", v.name().toLowerCase())),
                        Billboard.CENTER
                )).then(command.option(
                        "default-background", "enabled",
                        BoolArgumentType.bool(), Boolean.class,
                        (ch, v) -> ch.getTagOptions().setDefaultBackground(v),
                        v -> "character.tag.background",
                        v -> List.of(Placeholder.unparsed("value", String.valueOf(v))),
                        false
                )).then(command.option(
                        "line-width", "width",
                        IntegerArgumentType.integer(0), Integer.class,
                        (ch, v) -> ch.getTagOptions().setLineWidth(v),
                        v -> "character.tag.line-width",
                        v -> List.of(Placeholder.unparsed("value", String.valueOf(v))),
                        200
                )).then(command.option(
                        "text-opacity", "opacity",
                        FloatArgumentType.floatArg(0, 100), Float.class,
                        (ch, v) -> ch.getTagOptions().setTextOpacity(v),
                        v -> "character.tag.text-opacity",
                        v -> List.of(Formatter.number("value", v)),
                        0f
                )).then(command.option(
                        "scale", "scale",
                        FloatArgumentType.floatArg(0.05f, 10), Float.class,
                        (ch, v) -> ch.getTagOptions().setScale(v),
                        v -> "character.tag.scale",
                        v -> List.of(Formatter.number("value", v)),
                        1f
                )).then(command.option(
                        "see-through", "see-through",
                        BoolArgumentType.bool(), Boolean.class,
                        (ch, v) -> ch.getTagOptions().setSeeThrough(v),
                        v -> v ? "character.tag.see-through" : "character.tag.not-see-through",
                        v -> null,
                        false
                )).then(command.option(
                        "text-shadow", "enabled",
                        BoolArgumentType.bool(), Boolean.class,
                        (ch, v) -> ch.getTagOptions().setTextShadow(v),
                        v -> "character.tag.text-shadow",
                        v -> List.of(Placeholder.unparsed("value", String.valueOf(v))),
                        false
                )).then(command.option(
                        "visible", "visible",
                        BoolArgumentType.bool(), Boolean.class,
                        Character::setDisplayNameVisible,
                        v -> v ? "character.tag.visible" : "character.tag.invisible",
                        v -> null,
                        true
                ))
        );
    }

    private <T> ArgumentBuilder<CommandSourceStack, ?> option(
            @NonNull String name,
            @NonNull String argName,
            @NonNull ArgumentType<T> argType,
            @NonNull Class<T> argClass,
            @NonNull BiFunction<Character<?>, T, Boolean> setter,
            @NonNull Function<T, String> messageKey,
            @NonNull Function<T, @Nullable List<TagResolver>> placeholders,
            @Nullable T defaultValue
    ) {
        var node = Commands.literal(name);

        node.then(Commands.argument(argName, argType).executes(ctx -> {
            T value = ctx.getArgument(argName, argClass);
            return apply(ctx, value, setter, messageKey, placeholders);
        }));

        node.then(Commands.literal("reset").executes(ctx -> {
            return apply(ctx, defaultValue, setter, messageKey, placeholders);
        }));

        return node;
    }

    private <T> int apply(
            @NonNull CommandContext<CommandSourceStack> context,
            @Nullable T value,
            @NonNull BiFunction<Character<?>, T, Boolean> setter,
            @NonNull Function<T, String> messageKey,
            @NonNull Function<T, @Nullable List<TagResolver>> placeholders
    ) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);

        var success = setter.apply(character, value);
        var key = success ? messageKey.apply(value) : "nothing.changed";

        var extra = placeholders.apply(value);
        var all = new ArrayList<TagResolver>(extra != null ? extra.size() + 1 : 1);
        all.add(Placeholder.unparsed("character", character.getName()));
        if (extra != null) all.addAll(extra);

        plugin.bundle().sendMessage(sender, key, all.toArray(TagResolver[]::new));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
