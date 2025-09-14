package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static net.thenextlvl.character.plugin.command.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
final class CharacterActionChanceCommand extends BrigadierCommand {
    private CharacterActionChanceCommand(CharacterPlugin plugin) {
        super(plugin, "chance", "characters.command.action.chance");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionChanceCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin)
                        .then(command.chanceArgument().executes(command::set))
                        .executes(command::get)));
    }

    private int get(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName).orElse(null);
        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("action", actionName));
            return 0;
        }
        plugin.bundle().sendMessage(sender, "character.action.chance",
                Formatter.number("chance", action.getChance()),
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private int set(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName).orElse(null);
        var chance = context.getArgument("chance", int.class);

        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("action", actionName));
            return 0;
        }

        var success = !Objects.equals(action.getChance(), chance);
        if (success) action.setChance(chance);

        var message = success ? "character.action.chance.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Formatter.number("chance", chance));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> chanceArgument() {
        return Commands.argument("chance", IntegerArgumentType.integer(0, 100));
    }
}
