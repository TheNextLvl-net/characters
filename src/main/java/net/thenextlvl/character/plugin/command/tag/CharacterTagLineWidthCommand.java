package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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
final class CharacterTagLineWidthCommand extends SimpleCommand {
    private CharacterTagLineWidthCommand(CharacterPlugin plugin) {
        super(plugin, "line-width", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagLineWidthCommand(plugin);
        return command.create().then(Commands.argument(
                "width", IntegerArgumentType.integer(0)
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagLineWidthCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var width = tryGetArgument(context, "width", int.class).orElse(200);
        var success = character.getTagOptions().setLineWidth(width);
        var message = success ? "character.tag.line-width" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(width)));
        return success ? SINGLE_SUCCESS : 0;
    }
}
