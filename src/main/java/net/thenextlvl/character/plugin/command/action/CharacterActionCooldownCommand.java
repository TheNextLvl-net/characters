package net.thenextlvl.character.plugin.command.action;

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
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.action.CharacterActionCommand.actionArgument;

@NullMarked
final class CharacterActionCooldownCommand extends ActionCommand {
    private CharacterActionCooldownCommand(CharacterPlugin plugin) {
        super(plugin, "cooldown", "characters.command.action.cooldown");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionCooldownCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin)
                        .then(cooldownArgument().executes(command))
                        .executes(command)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> cooldownArgument() {
        return Commands.argument("cooldown", ArgumentTypes.time());
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context, Character<?> character, ClickAction<?> action, String actionName) {
        var cooldown = tryGetArgument(context, "cooldown", int.class).map(Tick::of).orElse(null);

        var success = cooldown != null && !Objects.equals(action.getCooldown(), cooldown);
        if (success) action.setCooldown(cooldown);

        var message = cooldown == null ? "character.action.cooldown"
                : success ? cooldown.isZero() ? "character.action.cooldown.removed"
                : "character.action.cooldown.set"
                : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Formatter.number("cooldown", (cooldown != null ? cooldown : action.getCooldown()).toMillis() / 1000d));
        return success ? SINGLE_SUCCESS : 0;
    }
}
