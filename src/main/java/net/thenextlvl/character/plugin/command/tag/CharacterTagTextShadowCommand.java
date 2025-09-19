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
final class CharacterTagTextShadowCommand extends SimpleCommand {
    private CharacterTagTextShadowCommand(CharacterPlugin plugin) {
        super(plugin, "text-shadow", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagTextShadowCommand(plugin);
        return command.create().then(Commands.argument(
                "enabled", BoolArgumentType.bool()
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagTextShadowCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var enabled = tryGetArgument(context, "enabled", boolean.class).orElse(false);
        var success = character.getTagOptions().setTextShadow(enabled);
        var message = success ? "character.tag.text-shadow" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", String.valueOf(enabled)));
        return success ? SINGLE_SUCCESS : 0;
    }
}
