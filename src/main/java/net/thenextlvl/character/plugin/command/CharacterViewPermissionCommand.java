package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.permissionArgument;

@NullMarked
final class CharacterViewPermissionCommand extends SimpleCommand {
    private CharacterViewPermissionCommand(final CharacterPlugin plugin) {
        super(plugin, "view-permission", "characters.command.view-permission");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterViewPermissionCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .then(Commands.literal("remove").executes(command::set))
                .then(permissionArgument(plugin).executes(command::set))
                .executes(command));
    }

    private int set(final CommandContext<CommandSourceStack> context) {
        final var character = context.getArgument("character", Character.class);
        final var viewPermission = tryGetArgument(context, "permission", String.class).orElse(null);
        final var success = character.setViewPermission(viewPermission);
        final var message = !success ? "nothing.changed" : viewPermission != null
                ? "character.view-permission.set" : "character.view-permission.removed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("permission", String.valueOf(viewPermission)));
        return success ? SINGLE_SUCCESS : 0;
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var character = (Character<?>) context.getArgument("character", Character.class);
        final var permission = character.getViewPermission().orElse(null);
        final var message = permission != null ? "character.view-permission" : "character.view-permission.none";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("permission", String.valueOf(permission)));
        return SINGLE_SUCCESS;
    }
}
