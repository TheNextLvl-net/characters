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
    private CharacterViewPermissionCommand(CharacterPlugin plugin) {
        super(plugin, "view-permission", "characters.command.view-permission");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterViewPermissionCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .then(permissionArgument(plugin).executes(command::set))
                .then(Commands.literal("remove").executes(command::set))
                .executes(command));
    }

    private int set(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var viewPermission = tryGetArgument(context, "permission", String.class).orElse(null);
        var success = character.setViewPermission(viewPermission);
        var message = !success ? "nothing.changed" : viewPermission != null
                ? "character.view-permission.set" : "character.view-permission.removed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("permission", String.valueOf(viewPermission)));
        return success ? SINGLE_SUCCESS : 0;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        plugin.bundle().sendMessage(context.getSource().getSender(), "character.view-permission",
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("permission", String.valueOf(character.getViewPermission())));
        return SINGLE_SUCCESS;
    }
}
