package net.thenextlvl.character.plugin.command.action;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.permissionArgument;
import static net.thenextlvl.character.plugin.command.action.CharacterActionCommand.actionArgument;

@NullMarked
final class CharacterActionPermissionCommand extends ActionCommand {
    private CharacterActionPermissionCommand(CharacterPlugin plugin) {
        super(plugin, "permission", "characters.command.action.permission");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionPermissionCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin)
                        .then(Commands.literal("remove").executes(command::set))
                        .then(permissionArgument(plugin).executes(command::set))
                        .executes(command)));
    }

    private int set(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var action = character.getAction(actionName).orElse(null);
        var permission = tryGetArgument(context, "permission", String.class).orElse(null);

        if (action == null) {
            plugin.bundle().sendMessage(sender, "character.action.not_found",
                    Placeholder.parsed("character", character.getName()),
                    Placeholder.unparsed("action", actionName));
            return 0;
        }

        var success = action.setPermission(permission);
        var message = success ? permission != null
                ? "character.action.permission.set" : "character.action.permission.removed"
                : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("permission", String.valueOf(permission)));
        return success ? SINGLE_SUCCESS : 0;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context, Character<?> character, ClickAction<?> action, String actionName) {
        var message = action.getPermission() != null ? "character.action.permission" : "character.action.permission.none";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("permission", String.valueOf(action.getPermission())),
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()));
        return SINGLE_SUCCESS;
    }
}
