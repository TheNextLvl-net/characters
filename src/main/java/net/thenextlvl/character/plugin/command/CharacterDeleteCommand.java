package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
final class CharacterDeleteCommand extends SimpleCommand {
    private CharacterDeleteCommand(CharacterPlugin plugin) {
        super(plugin, "delete", "characters.command.delete");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterDeleteCommand(plugin);
        return command.create().then(characterArgument(plugin).executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        character.delete();
        plugin.bundle().sendMessage(context.getSource().getSender(), "character.deleted",
                Placeholder.unparsed("character", character.getName()));
        return SINGLE_SUCCESS;
    }
}
