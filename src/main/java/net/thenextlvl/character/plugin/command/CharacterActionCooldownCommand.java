package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterActionCooldownCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("cooldown")
                .then(remove(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> remove(CharacterPlugin plugin) {
        return Commands.literal("remove").then(characterArgument(plugin).then(actionArgument(plugin)
                .executes(context -> remove(context, plugin))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin).then(actionArgument(plugin)
                .then(cooldownArgument(plugin)).executes(context -> set(context, plugin))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> cooldownArgument(CharacterPlugin plugin) {
        return Commands.argument("cooldown", ArgumentTypes.time());
    }

    private static int remove(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        return 0;
    }

    private static int set(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        return 0;
    }
}
