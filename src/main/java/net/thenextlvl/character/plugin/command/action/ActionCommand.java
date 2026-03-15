package net.thenextlvl.character.plugin.command.action;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
abstract class ActionCommand extends SimpleCommand {
    protected ActionCommand(final CharacterPlugin plugin, final String name, @Nullable final String permission) {
        super(plugin, name, permission);
    }

    @Override
    public final int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var sender = context.getSource().getSender();
        final var character = (Character<?>) context.getArgument("character", Character.class);
        final var actionName = context.getArgument("action", String.class);
        final var action = character.getAction(actionName).orElse(null);
        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("action", actionName));
            return 0;
        }
        return run(context, character, action, actionName);
    }

    public abstract int run(CommandContext<CommandSourceStack> context, Character<?> character, ClickAction<?> action, String actionName) throws CommandSyntaxException;
}
