package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterAttributeCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("attribute").then(reset(plugin)).then(set(plugin));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        return Commands.literal("reset").then(characterArgument(plugin)
                .then(resetAI(plugin))
                .then(resetColliding(plugin))
                .then(resetGlowing(plugin))
                .then(resetGravity(plugin))
                .then(resetPathfinding(plugin))
                .then(resetTicking(plugin)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(setAI(plugin))
                .then(setColliding(plugin))
                .then(setGlowing(plugin))
                .then(setGravity(plugin))
                .then(setPathfinding(plugin))
                .then(setTicking(plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(String name, Function<Character<?>, Boolean> function, CharacterPlugin plugin) {
        return Commands.literal(name).executes(context -> {
            var character = context.getArgument("character", Character.class);
            var success = function.apply(character);
            return success ? Command.SINGLE_SUCCESS : 0;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> attribute(String name, BiFunction<Character<?>, Boolean, Boolean> function, CharacterPlugin plugin) {
        return Commands.literal(name).then(Commands.argument("enabled", BoolArgumentType.bool()).executes(context -> {
            var character = context.getArgument("character", Character.class);
            var enabled = context.getArgument("enabled", boolean.class);
            var success = function.apply(character, enabled);
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetAI(CharacterPlugin plugin) {
        return reset("ai", character -> character.setAI(false), plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetColliding(CharacterPlugin plugin) {
        return reset("colliding", character -> character.setCollidable(false), plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetGlowing(CharacterPlugin plugin) {
        return reset("glowing", character -> character.setGlowing(false), plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetGravity(CharacterPlugin plugin) {
        return reset("gravity", character -> character.setGravity(false), plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetPathfinding(CharacterPlugin plugin) {
        return reset("pathfinding", character -> character.setPathfinding(false), plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resetTicking(CharacterPlugin plugin) {
        return reset("ticking", character -> character.setTicking(false), plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setAI(CharacterPlugin plugin) {
        return attribute("ai", Character::setAI, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setColliding(CharacterPlugin plugin) {
        return attribute("colliding", Character::setCollidable, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setGlowing(CharacterPlugin plugin) {
        return attribute("glowing", Character::setGlowing, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setGravity(CharacterPlugin plugin) {
        return attribute("gravity", Character::setGravity, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setPathfinding(CharacterPlugin plugin) {
        return attribute("pathfinding", Character::setPathfinding, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setTicking(CharacterPlugin plugin) {
        return attribute("ticking", Character::setTicking, plugin);
    }
}
