package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterListCommand extends SimpleCommand {
    private CharacterListCommand(CharacterPlugin plugin) {
        super(plugin, "list", "characters.command.list");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterListCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var characters = plugin.characterController().getCharacters();
        if (characters.isEmpty()) plugin.bundle().sendMessage(sender, "character.list.empty");
        else characters.forEach(character -> plugin.bundle().sendMessage(sender, "character.list",
                Placeholder.parsed("character", character.getName()),
                Placeholder.unparsed("type", character.getType().key().asString())));
        return SINGLE_SUCCESS;
    }
}
