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
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterTagOffsetCommand extends SimpleCommand {
    private CharacterTagOffsetCommand(CharacterPlugin plugin) {
        super(plugin, "offset", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagOffsetCommand(plugin);
        return command.create().then(Commands.argument(
                "x", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "y", FloatArgumentType.floatArg()
        ).then(Commands.argument(
                "z", FloatArgumentType.floatArg()
        ).executes(command))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagOffsetCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
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
        return success ? SINGLE_SUCCESS : 0;
    }
}
