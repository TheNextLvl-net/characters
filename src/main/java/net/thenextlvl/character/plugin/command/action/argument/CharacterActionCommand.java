package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import net.thenextlvl.character.plugin.model.ClickTypes;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
abstract class CharacterActionCommand<T> extends SimpleCommand {
    private final ActionType<T> actionType;

    protected CharacterActionCommand(final CharacterPlugin plugin, final ActionType<T> actionType, final String name) {
        super(plugin, name, null);
        this.actionType = actionType;
    }

    protected int addAction(final CommandContext<CommandSourceStack> context, final T input) {
        final var sender = context.getSource().getSender();
        final var character = (net.thenextlvl.character.Character<?>) context.getArgument("character", Character.class);
        final var actionName = context.getArgument("action", String.class);
        final var clickTypes = context.getArgument("click-types", ClickTypes.class);

        final var previous = character.getAction(actionName);
        final var cooldown = previous.map(ClickAction::getCooldown).orElse(Duration.ZERO);
        final var permission = previous.map(ClickAction::getPermission).orElse(null);
        final var chance = previous.map(ClickAction::getChance).orElse(100);

        final var success = character.addAction(actionName, ClickAction.create(actionType, clickTypes.getClickTypes(), input, action -> {
            action.setChance(chance);
            action.setCooldown(cooldown);
            action.setPermission(permission);
        }));

        final var message = success ? "character.action.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
