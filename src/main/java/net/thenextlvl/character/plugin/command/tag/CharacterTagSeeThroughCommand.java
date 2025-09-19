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
final class CharacterTagSeeThroughCommand extends SimpleCommand {
    private CharacterTagSeeThroughCommand(CharacterPlugin plugin) {
        super(plugin, "see-through", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagSeeThroughCommand(plugin);
        return command.create().then(Commands.argument(
                "see-through", BoolArgumentType.bool()
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagSeeThroughCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var seeThrough = tryGetArgument(context, "see-through", boolean.class).orElse(false);
        var success = character.getTagOptions().setSeeThrough(seeThrough);
        var message = !success ? "nothing.changed" : seeThrough
                ? "character.tag.see-through" : "character.tag.not-see-through";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
