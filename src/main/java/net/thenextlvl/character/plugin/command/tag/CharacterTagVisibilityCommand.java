package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterTagVisibilityCommand extends SimpleCommand {
    private CharacterTagVisibilityCommand(final CharacterPlugin plugin) {
        super(plugin, "visibility", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(final CharacterPlugin plugin) {
        final var command = new CharacterTagVisibilityCommand(plugin);
        return command.create().then(Commands.argument(
                "visible", BoolArgumentType.bool()
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(final CharacterPlugin plugin) {
        final var command = new CharacterTagVisibilityCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var character = context.getArgument("character", Character.class);
        final var visible = tryGetArgument(context, "visible", boolean.class).orElse(true);

        final var success = character.setDisplayNameVisible(visible);
        final var message = !success ? "nothing.changed" : visible ? "character.tag.visible" : "character.tag.invisible";

        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
