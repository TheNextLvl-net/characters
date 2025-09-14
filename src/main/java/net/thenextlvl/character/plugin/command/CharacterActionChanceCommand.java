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
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static net.thenextlvl.character.plugin.command.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterActionChanceCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("chance")
                .requires(source -> source.getSender().hasPermission("characters.command.action.chance"))
                .then(characterArgument(plugin)
                        .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                        .then(actionArgument(plugin)
                                .then(chanceArgument(plugin).executes(context -> set(context, plugin)))
                                .executes(context -> get(context, plugin))));
    }

    private static int get(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName).orElse(null);
        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("name", actionName));
            return 0;
        }
        plugin.bundle().sendMessage(sender, "character.action.chance",
                Formatter.number("chance", action.getChance()),
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private static int set(CommandContext<CommandSourceStack> context, int chance, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName).orElse(null);

        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("name", actionName));
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

    private static ArgumentBuilder<CommandSourceStack, ?> chanceArgument(CharacterPlugin plugin) {
        return Commands.argument("chance", IntegerArgumentType.integer(0, 100));
    }

    private static int set(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var chance = context.getArgument("chance", int.class);
        return set(context, chance, plugin);
    }
}
