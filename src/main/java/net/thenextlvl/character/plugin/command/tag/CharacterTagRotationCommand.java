package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import net.thenextlvl.character.tag.TagOptions;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;

@NullMarked
final class CharacterTagRotationCommand extends SimpleCommand {
    private final Rotation rotation;

    private CharacterTagRotationCommand(CharacterPlugin plugin, Rotation rotation) {
        super(plugin, rotation.name, null);
        this.rotation = rotation;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin, Rotation rotation) {
        var command = new CharacterTagRotationCommand(plugin, rotation);
        return command.create().then(Commands.argument(
                "x", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "y", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "z", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "w", FloatArgumentType.floatArg()
        ).executes(command)).executes(command))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin, Rotation rotation) {
        var command = new CharacterTagRotationCommand(plugin, rotation);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var w = tryGetArgument(context, "w", float.class).orElse(1.0f);
        var x = tryGetArgument(context, "x", float.class).orElse(0.0f);
        var y = tryGetArgument(context, "y", float.class).orElse(0.0f);
        var z = tryGetArgument(context, "z", float.class).orElse(0.0f);
        var success = rotation.setter.apply(character.getTagOptions(), new Quaternionf(x, y, z, w));
        var message = success ? rotation.success : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Formatter.number("w", w),
                Formatter.number("x", x),
                Formatter.number("y", y),
                Formatter.number("z", z));
        return success ? SINGLE_SUCCESS : 0;
    }

    public enum Rotation {
        LEFT("left-rotation", "character.tag.left-rotation", TagOptions::setLeftRotation),
        RIGHT("right-rotation", "character.tag.right-rotation", TagOptions::setRightRotation);

        private final String name;
        private final String success;
        private final BiFunction<TagOptions, Quaternionf, Boolean> setter;

        Rotation(String name, String success, BiFunction<TagOptions, Quaternionf, Boolean> setter) {
            this.name = name;
            this.success = success;
            this.setter = setter;
        }
    }
}
