package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.entity.TeleportFlag.EntityState;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterTeleportCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var teleport = characterArgument(plugin).executes(context -> teleportSelf(context, plugin))
                .then(positionArgument(plugin).executes(context -> teleportPosition(context, plugin)))
                .then(entityArgument(plugin).executes(context -> teleportEntity(context, plugin)));
        return Commands.literal("teleport").then(teleport);
    }

    private static int teleportSelf(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();

        if (!(sender instanceof Player player)) {
            plugin.bundle().sendMessage(sender, "command.sender");
            return 0;
        }

        var character = (Character<?>) context.getArgument("character", Character.class);

        var location = character.getEntity().map(Entity::getLocation).orElse(character.getSpawnLocation());
        if (location != null) player.teleportAsync(location, TeleportCause.COMMAND, EntityState.RETAIN_PASSENGERS);

        plugin.bundle().sendMessage(sender, "character.teleported.self",
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> positionArgument(CharacterPlugin plugin) {
        return Commands.argument("position", ArgumentTypes.finePosition(true));
    }

    private static int teleportPosition(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) throws CommandSyntaxException {
        var resolver = context.getArgument("position", FinePositionResolver.class);
        var position = resolver.resolve(context.getSource()).toLocation(context.getSource().getLocation().getWorld());
        return teleport(context, plugin, position);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> entityArgument(CharacterPlugin plugin) {
        return Commands.argument("entity", ArgumentTypes.entity());
    }

    private static int teleportEntity(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) throws CommandSyntaxException {
        var selector = context.getArgument("entity", EntitySelectorArgumentResolver.class);
        var entity = selector.resolve(context.getSource()).getFirst();
        return teleport(context, plugin, entity.getLocation());
    }

    private static int teleport(CommandContext<CommandSourceStack> context, CharacterPlugin plugin, Location location) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);

        character.getEntity().ifPresent(entity -> entity.teleportAsync(location));
        character.setSpawnLocation(location);

        plugin.bundle().sendMessage(sender, "character.teleported",
                Placeholder.unparsed("world", location.getWorld().getName()),
                Formatter.number("x", location.x()),
                Formatter.number("y", location.y()),
                Formatter.number("z", location.z()),
                Formatter.number("yaw", location.getYaw()),
                Formatter.number("pitch", location.getPitch()),
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
