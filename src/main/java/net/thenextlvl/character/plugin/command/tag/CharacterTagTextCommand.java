package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterTagTextCommand extends SimpleCommand {
    private CharacterTagTextCommand(CharacterPlugin plugin) {
        super(plugin, "text", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagTextCommand(plugin);
        return command.create().then(Commands.argument(
                "text", StringArgumentType.greedyString()
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagTextCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);
        var text = tryGetArgument(context, "text", String.class).map(MiniMessage.miniMessage()::deserialize).orElse(null);

        var success = character.setDisplayName(text);
        var message = success ? "character.tag.text" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.component("text", text != null ? text : Component.text(character.getName())),
                Placeholder.unparsed("character", character.getName()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
