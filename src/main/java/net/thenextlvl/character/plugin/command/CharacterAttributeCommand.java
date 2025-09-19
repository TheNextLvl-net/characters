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
import net.thenextlvl.character.plugin.command.argument.NamedTextColorArgumentType;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

// todo: split up into multiple commands
@NullMarked
final class CharacterAttributeCommand extends BrigadierCommand {
    private CharacterAttributeCommand(CharacterPlugin plugin) {
        super(plugin, "attribute", "characters.command.attribute");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterAttributeCommand(plugin);
        return command.create()
                .then(command.reset())
                .then(command.set());
    }

    private LiteralArgumentBuilder<CommandSourceStack> reset() {
        var tree = characterArgument(plugin)
                .then(resetListed())
                .then(resetPathfinding())
                .then(resetTeamColor());
        // fixme
        //  EntityCodecs.types().forEach(type -> tree.then(resetAttribute(type, plugin)));
        return Commands.literal("reset").then(tree);
    }

    private LiteralArgumentBuilder<CommandSourceStack> set() {
        var tree = characterArgument(plugin)
                .then(setListed())
                .then(setPathfinding())
                .then(setTeamColor());
        EntityCodecRegistry.registry().codecs().forEach(codec -> {
            var argument = setAttribute(codec);
            if (argument != null) tree.then(argument);
        });
        return Commands.literal("set").then(tree);
    }

    // private <E, T> ArgumentBuilder<CommandSourceStack, ?> resetAttribute(AttributeType<E, T> attribute) {
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

    private <E, T> @Nullable ArgumentBuilder<CommandSourceStack, ?> setAttribute(EntityCodec<E, T> codec) {
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

    private ArgumentBuilder<CommandSourceStack, ?> attribute(String attribute, BiFunction<Character<?>, Boolean, Boolean> setter) {
        return Commands.literal(attribute).then(Commands.argument("enabled", BoolArgumentType.bool()).executes(context -> {
            var enabled = context.getArgument("enabled", boolean.class);
            var success = set(context, attribute,
                    character -> setter.apply(character, enabled),
                    ignored -> enabled);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private ArgumentBuilder<CommandSourceStack, ?> reset(String attribute, Function<Character<?>, Boolean> setter, Function<Character<?>, @Nullable Object> getter) {
        return Commands.literal(attribute).executes(context -> {
            var success = set(context, attribute, setter, getter);
            return success ? Command.SINGLE_SUCCESS : 0;
        });
    }

    private boolean set(CommandContext<CommandSourceStack> context, String attribute, Function<Character<?>, Boolean> setter, Function<Character<?>, @Nullable Object> getter) {
        var character = context.getArgument("character", Character.class);
        var success = setter.apply(character);
        var message = success ? "character.attribute" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("attribute", attribute),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(getter.apply(character))));
        return success;
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetListed() {
        return reset("character:listed", character -> character instanceof PlayerCharacter p && p.setListed(false),
                character -> character instanceof PlayerCharacter p && p.isListed());
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetPathfinding() {
        return reset("character:pathfinding", character -> character.setPathfinding(false), Character::isPathfinding);
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetTeamColor() {
        return reset("character:team-color", character -> character.setTeamColor(null), Character::getTeamColor);
    }

    private ArgumentBuilder<CommandSourceStack, ?> setListed() {
        return attribute("character:listed", (c, b) -> c instanceof PlayerCharacter p && p.setListed(b));
    }

    private ArgumentBuilder<CommandSourceStack, ?> setPathfinding() {
        return attribute("character:pathfinding", Character::setPathfinding);
    }

    private ArgumentBuilder<CommandSourceStack, ?> setTeamColor() {
        return Commands.literal("character:team-color").then(Commands.argument("color", new NamedTextColorArgumentType())
                .executes(context -> {
                    var color = context.getArgument("color", NamedTextColor.class);
                    var success = set(context, "team-color",
                            character -> character.setTeamColor(color),
                            character -> color);
                    return success ? Command.SINGLE_SUCCESS : 0;
                }));
    }
}
