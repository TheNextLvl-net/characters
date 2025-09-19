package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.ColorArgumentType;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterTagBackgroundColorCommand extends SimpleCommand {
    private CharacterTagBackgroundColorCommand(CharacterPlugin plugin) {
        super(plugin, "background-color", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagBackgroundColorCommand(plugin);
        return command.create().then(Commands.argument(
                "color", new ColorArgumentType()
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagBackgroundColorCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var color = tryGetArgument(context, "color", Color.class).orElse(null);
        var success = character.getTagOptions().setBackgroundColor(color);
        var message = success ? "character.tag.background-color" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", color != null ? "#" + Integer.toHexString(color.asARGB()) : "null"));
        return success ? SINGLE_SUCCESS : 0;
    }
}
