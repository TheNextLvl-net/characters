package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.util.Tick;
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
class CharacterActionCooldownCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("cooldown")
                .requires(source -> source.getSender().hasPermission("characters.command.action.cooldown"))
                .then(characterArgument(plugin)
                        .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                        .then(actionArgument(plugin)
                                .then(cooldownArgument(plugin))
                                .executes(context -> get(context, plugin))));
    }

    private static int get(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName);
        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("name", actionName));
            return 0;
        }
        plugin.bundle().sendMessage(sender, "character.action.cooldown",
                Placeholder.parsed("character", character.getName()),
                Placeholder.parsed("action", actionName),
                Formatter.number("cooldown", action.getCooldown().toSeconds()));
        return Command.SINGLE_SUCCESS;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> cooldownArgument(CharacterPlugin plugin) {
        return Commands.argument("cooldown", ArgumentTypes.time())
                .executes(context -> set(context, plugin));
    }

    private static int set(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName);

        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("name", actionName));
            return 0;
        }

        var cooldown = Tick.of(context.getArgument("cooldown", int.class));

        var success = !Objects.equals(action.getCooldown(), cooldown);
        if (success) action.setCooldown(cooldown);

        var message = success ? cooldown.isZero()
                ? "character.action.cooldown.removed" : "character.action.cooldown.set"
                : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Formatter.number("cooldown", cooldown.toSeconds()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
