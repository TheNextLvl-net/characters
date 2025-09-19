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
    
    protected CharacterActionCommand(CharacterPlugin plugin, ActionType<T> actionType, String name) {
        super(plugin, name, null);
        this.actionType = actionType;
    }

    protected int addAction(CommandContext<CommandSourceStack> context, T input) {
        var sender = context.getSource().getSender();
        var character = (net.thenextlvl.character.Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var clickTypes = context.getArgument("click-types", ClickTypes.class);

        var previous = character.getAction(actionName);
        var cooldown = previous.map(ClickAction::getCooldown).orElse(Duration.ZERO);
        var permission = previous.map(ClickAction::getPermission).orElse(null);
        var chance = previous.map(ClickAction::getChance).orElse(100);

        var success = character.addAction(actionName, ClickAction.create(actionType, clickTypes.getClickTypes(), input, action -> {
            action.setChance(chance);
            action.setCooldown(cooldown);
            action.setPermission(permission);
        }));

        var message = success ? "character.action.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
