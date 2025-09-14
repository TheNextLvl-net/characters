package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.codec.EntityCodec;
import net.thenextlvl.character.codec.EntityCodecRegistry;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.NamedTextColorArgument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterAttributeCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("attribute")
                .requires(source -> source.getSender().hasPermission("characters.command.attribute"))
                .then(reset(plugin)).then(set(plugin));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var tree = characterArgument(plugin)
                .then(resetListed(plugin))
                .then(resetPathfinding(plugin))
                .then(resetTeamColor(plugin));
        // fixme
        //  EntityCodecs.types().forEach(type -> tree.then(resetAttribute(type, plugin)));
        return Commands.literal("reset").then(tree);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var tree = characterArgument(plugin)
                .then(setListed(plugin))
                .then(setPathfinding(plugin))
                .then(setTeamColor(plugin));
        EntityCodecRegistry.registry().codecs().forEach(codec -> {
            var argument = setAttribute(codec, plugin);
            if (argument != null) tree.then(argument);
        });
        return Commands.literal("set").then(tree);
    }

    // private static <E, T> ArgumentBuilder<CommandSourceStack, ?> resetAttribute(AttributeType<E, T> attribute, CharacterPlugin plugin) {
    //     return Commands.literal(attribute.key().asString()).executes(context -> {
    //         var character = (Character<?>) context.getArgument("character", Character.class);
    //         var success = character.setAttributeValue(attribute, null);
    //         var message = success ? "character.attribute" : "nothing.changed";
    //         plugin.bundle().sendMessage(context.getSource().getSender(), message,
    //                 Placeholder.unparsed("attribute", attribute.key().asString()),
    //                 Placeholder.unparsed("character", character.getName()),
    //                 Placeholder.unparsed("value", character.getAttributeValue(attribute).map(Object::toString).orElse("null")));
    //         return success ? Command.SINGLE_SUCCESS : 0;
    //     });
    // }

    private static <E, T> @Nullable ArgumentBuilder<CommandSourceStack, ?> setAttribute(EntityCodec<E, T> codec, CharacterPlugin plugin) {
        if (codec.argumentType() == null) return null;
        var argument = Commands.argument("value", codec.argumentType());
        return Commands.literal(codec.key().asString()).then(argument.executes(context -> {
            var value = context.getArgument("value", codec.valueType());
            var character = (Character<?>) context.getArgument("character", Character.class);
            var success = character.getEntity(codec.entityType()).map(entity -> codec.setter().test(entity, value)).orElse(false);
            var message = success ? "character.attribute" : "nothing.changed";
            var split = context.getInput().split(" ", 6); // trickery to display the unparsed value
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    Placeholder.unparsed("attribute", codec.key().asString()),
                    Placeholder.unparsed("character", character.getName()),
                    Placeholder.unparsed("value", split[split.length - 1]));
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> attribute(String attribute, BiFunction<Character<?>, Boolean, Boolean> setter, CharacterPlugin plugin) {
        return Commands.literal(attribute).then(Commands.argument("enabled", BoolArgumentType.bool()).executes(context -> {
            var enabled = context.getArgument("enabled", boolean.class);
            var success = set(context, attribute,
                    character -> setter.apply(character, enabled),
                    ignored -> enabled, plugin);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(String attribute, Function<Character<?>, Boolean> setter, Function<Character<?>, @Nullable Object> getter, CharacterPlugin plugin) {
        return Commands.literal(attribute).executes(context -> {
            var success = set(context, attribute, setter, getter, plugin);
            return success ? Command.SINGLE_SUCCESS : 0;
        });
    }

    private static boolean set(CommandContext<CommandSourceStack> context, String attribute, Function<Character<?>, Boolean> setter, Function<Character<?>, @Nullable Object> getter, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = setter.apply(character);
        var message = success ? "character.attribute" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("attribute", attribute),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(getter.apply(character))));
        return success;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetListed(CharacterPlugin plugin) {
        return reset("character:listed", character -> character instanceof PlayerCharacter p && p.setListed(false),
                character -> character instanceof PlayerCharacter p && p.isListed(), plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetPathfinding(CharacterPlugin plugin) {
        return reset("character:pathfinding", character -> character.setPathfinding(false), Character::isPathfinding, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetTeamColor(CharacterPlugin plugin) {
        return reset("character:team-color", character -> character.setTeamColor(null), Character::getTeamColor, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setListed(CharacterPlugin plugin) {
        return attribute("character:listed", (c, b) -> c instanceof PlayerCharacter p && p.setListed(b), plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setPathfinding(CharacterPlugin plugin) {
        return attribute("character:pathfinding", Character::setPathfinding, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setTeamColor(CharacterPlugin plugin) {
        return Commands.literal("character:team-color").then(Commands.argument("color", new NamedTextColorArgument())
                .executes(context -> {
                    var color = context.getArgument("color", NamedTextColor.class);
                    var success = set(context, "team-color",
                            character -> character.setTeamColor(color),
                            character -> color, plugin);
                    return success ? Command.SINGLE_SUCCESS : 0;
                }));
    }
}
