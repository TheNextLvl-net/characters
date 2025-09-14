package net.thenextlvl.character.plugin.command.action;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.action.CharacterActionCommand.actionArgument;

@NullMarked
final class CharacterActionChanceCommand extends ActionCommand {
    private CharacterActionChanceCommand(CharacterPlugin plugin) {
        super(plugin, "chance", "characters.command.action.chance");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionChanceCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin)
                        .then(chanceArgument().executes(command))
                        .executes(command)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> chanceArgument() {
        return Commands.argument("chance", IntegerArgumentType.integer(0, 100));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context, Character<?> character, ClickAction<?> action, String actionName) {
        var chance = tryGetArgument(context, "chance", int.class).orElse(null);

        var success = chance != null && !Objects.equals(action.getChance(), chance);
        if (success) action.setChance(chance);

        var message = chance == null ? "character.action.chance"
                : success ? "character.action.chance.set" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Formatter.number("chance", chance != null ? chance : action.getChance()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
