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
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterTagTextOpacityCommand extends SimpleCommand {
    private CharacterTagTextOpacityCommand(final CharacterPlugin plugin) {
        super(plugin, "text-opacity", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(final CharacterPlugin plugin) {
        final var command = new CharacterTagTextOpacityCommand(plugin);
        return command.create().then(Commands.argument(
                "opacity", FloatArgumentType.floatArg(0, 100)
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(final CharacterPlugin plugin) {
        final var command = new CharacterTagTextOpacityCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var character = context.getArgument("character", Character.class);
        final var opacity = tryGetArgument(context, "opacity", float.class).orElse(0f);
        final var success = character.getTagOptions().setTextOpacity(opacity);
        final var message = success ? "character.tag.text-opacity" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Formatter.number("value", opacity),
                Placeholder.unparsed("character", character.getName()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
