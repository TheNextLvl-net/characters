package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterActionCooldownCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("cooldown").then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin).then(cooldownArgument(plugin)
                        .executes(context -> set(context, plugin)))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> cooldownArgument(CharacterPlugin plugin) {
        return Commands.argument("cooldown", ArgumentTypes.time());
    }

    private static int set(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        return 0; // todo implement
    }
}
