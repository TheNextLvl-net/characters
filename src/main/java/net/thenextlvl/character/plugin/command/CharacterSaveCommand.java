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
final class CharacterSaveCommand extends SimpleCommand {
    private CharacterSaveCommand(CharacterPlugin plugin) {
        super(plugin, "save", "characters.command.save");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterSaveCommand(plugin);
        return command.create().then(characterArgument(plugin).executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var sender = context.getSource().getSender();
        var success = character.isPersistent() && character.persist();
        var message = !character.isPersistent() ? "character.not_persistent"
                : success ? "character.persisted" : "character.persisted.failed";
        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
