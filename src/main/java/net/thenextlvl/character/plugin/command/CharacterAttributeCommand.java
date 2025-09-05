package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.util.TriState;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.attribute.AttributeType;
import net.thenextlvl.character.attribute.AttributeTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.BlockDataArgument;
import net.thenextlvl.character.plugin.command.argument.ColorArgument;
import net.thenextlvl.character.plugin.command.argument.DurationArgument;
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import net.thenextlvl.character.plugin.command.argument.NamedTextColorArgument;
import net.thenextlvl.character.plugin.command.argument.ParticleArgument;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
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
        AttributeTypes.types().forEach(type -> tree.then(resetAttribute(type, plugin)));
        return Commands.literal("reset").then(tree);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var tree = characterArgument(plugin)
                .then(setListed(plugin))
                .then(setPathfinding(plugin))
                .then(setTeamColor(plugin));
        AttributeTypes.types().forEach(type -> tree.then(setAttribute(type, plugin)));
        return Commands.literal("set").then(tree);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ArgumentType<?> getArgumentType(Class<?> type) {
        if (type.equals(BlockData.class)) return new BlockDataArgument();
        if (type.equals(Cat.Type.class)) return ArgumentTypes.resource(RegistryKey.CAT_VARIANT);
        if (type.equals(Color.class)) return new ColorArgument();
        if (type.equals(Duration.class)) return new DurationArgument();
        if (type.equals(DyeColor.class)) return new EnumArgument<>(DyeColor.class);
        if (type.equals(Fox.Type.class)) return new EnumArgument<>(Fox.Type.class);
        if (type.equals(Frog.Variant.class)) return ArgumentTypes.resource(RegistryKey.FROG_VARIANT);
        if (type.equals(ItemStack.class)) return ArgumentTypes.itemStack();
        if (type.equals(Particle.class)) return new ParticleArgument();
        if (type.equals(PotionType.class)) return ArgumentTypes.resource(RegistryKey.POTION);
        if (type.equals(String.class)) return StringArgumentType.string();
        if (type.equals(TriState.class)) return new EnumArgument<>(TriState.class);
        if (type.equals(boolean.class) || type.equals(Boolean.class)) return BoolArgumentType.bool();
        if (type.equals(double.class) || type.equals(Double.class)) return DoubleArgumentType.doubleArg();
        if (type.equals(float.class) || type.equals(Float.class)) return FloatArgumentType.floatArg();
        if (type.equals(int.class) || type.equals(Integer.class)) return IntegerArgumentType.integer();
        if (type.equals(long.class) || type.equals(Long.class)) return LongArgumentType.longArg();
        if (type.isEnum()) return new EnumArgument(type);
        throw new IllegalArgumentException("Unexpected attribute type: " + type.getName());
    }

    private static <E, T> ArgumentBuilder<CommandSourceStack, ?> resetAttribute(AttributeType<E, T> attribute, CharacterPlugin plugin) {
        // todo: reset attribute to default value
        return Commands.literal(attribute.key().asString());
    }

    private static <E, T> ArgumentBuilder<CommandSourceStack, ?> setAttribute(AttributeType<E, T> attribute, CharacterPlugin plugin) {
        var argument = Commands.argument("value", getArgumentType(attribute.dataType()));
        return Commands.literal(attribute.key().asString()).then(argument.executes(context -> {
            var value = context.getArgument("value", attribute.dataType());
            var character = (Character<?>) context.getArgument("character", Character.class);
            var success = character.setAttributeValue(attribute, value);
            var message = success ? "character.attribute" : "nothing.changed";
            var split = context.getInput().split(" ", 6); // trickery to display the unparsed value
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    Placeholder.unparsed("attribute", attribute.key().asString()),
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
