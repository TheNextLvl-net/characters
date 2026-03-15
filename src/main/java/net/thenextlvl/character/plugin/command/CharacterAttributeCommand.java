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
    private CharacterAttributeCommand(final CharacterPlugin plugin) {
        super(plugin, "attribute", "characters.command.attribute");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterAttributeCommand(plugin);
        return command.create()
                .then(command.reset())
                .then(command.set());
    }

    private LiteralArgumentBuilder<CommandSourceStack> reset() {
        final var tree = characterArgument(plugin)
                .then(resetTeamColor());
        // fixme
        //  EntityCodecs.types().forEach(type -> tree.then(resetAttribute(type, plugin)));
        return Commands.literal("reset").then(tree);
    }

    private LiteralArgumentBuilder<CommandSourceStack> set() {
        final var tree = characterArgument(plugin)
                .then(setTeamColor());
        EntityCodecRegistry.registry().codecs().forEach(codec -> {
            final var argument = setAttribute(codec);
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

    private <E, T> @Nullable ArgumentBuilder<CommandSourceStack, ?> setAttribute(final EntityCodec<E, T> codec) {
        if (codec.argumentType() == null) return null;
        final var argument = Commands.argument("value", codec.argumentType());
        return Commands.literal(codec.key().asString()).then(argument.executes(context -> {
            final var value = context.getArgument("value", codec.valueType());
            final var character = (Character<?>) context.getArgument("character", Character.class);
            final var success = character.getEntity(codec.entityType()).map(entity -> codec.setter().test(entity, value)).orElse(false);
            final var message = success ? "character.attribute" : "nothing.changed";
            final var split = context.getInput().split(" ", 6); // trickery to display the unparsed value
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    Placeholder.unparsed("attribute", codec.key().asString()),
                    Placeholder.unparsed("character", character.getName()),
                    Placeholder.unparsed("value", split[split.length - 1]));
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private ArgumentBuilder<CommandSourceStack, ?> attribute(final String attribute, final BiFunction<Character<?>, Boolean, Boolean> setter) {
        return Commands.literal(attribute).then(Commands.argument("enabled", BoolArgumentType.bool()).executes(context -> {
            final var enabled = context.getArgument("enabled", boolean.class);
            final var success = set(context, attribute,
                    character -> setter.apply(character, enabled),
                    ignored -> enabled);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private ArgumentBuilder<CommandSourceStack, ?> reset(final String attribute, final Function<Character<?>, Boolean> setter, final Function<Character<?>, @Nullable Object> getter) {
        return Commands.literal(attribute).executes(context -> {
            final var success = set(context, attribute, setter, getter);
            return success ? Command.SINGLE_SUCCESS : 0;
        });
    }

    private boolean set(final CommandContext<CommandSourceStack> context, final String attribute, final Function<Character<?>, Boolean> setter, final Function<Character<?>, @Nullable Object> getter) {
        final var character = context.getArgument("character", Character.class);
        final var success = setter.apply(character);
        final var message = success ? "character.attribute" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("attribute", attribute),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(getter.apply(character))));
        return success;
    }

    private ArgumentBuilder<CommandSourceStack, ?> resetTeamColor() {
        return reset("character:team-color", character -> character.setTeamColor(null), Character::getTeamColor);
    }

    private ArgumentBuilder<CommandSourceStack, ?> setTeamColor() {
        return Commands.literal("character:team-color").then(Commands.argument("color", new NamedTextColorArgumentType())
                .executes(context -> {
                    final var color = context.getArgument("color", NamedTextColor.class);
                    final var success = set(context, "team-color",
                            character -> character.setTeamColor(color),
                            character -> color);
                    return success ? Command.SINGLE_SUCCESS : 0;
                }));
    }
}
