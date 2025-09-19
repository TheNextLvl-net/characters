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
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import static io.papermc.paper.entity.TeleportFlag.EntityState.RETAIN_PASSENGERS;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND;

@NullMarked
final class CharacterTeleportCommand extends BrigadierCommand {
    private CharacterTeleportCommand(CharacterPlugin plugin) {
        super(plugin, "teleport", "characters.command.teleport");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterTeleportCommand(plugin);
        var teleport = characterArgument(plugin).executes(command::teleportSelf)
                .then(positionArgument().executes(command::teleportPosition))
                .then(entityArgument().executes(command::teleportEntity));
        return command.create().then(teleport);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> entityArgument() {
        return Commands.argument("entity", ArgumentTypes.entity());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> positionArgument() {
        return Commands.argument("position", ArgumentTypes.finePosition(true));
    }

    private int teleportSelf(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();

        if (!(sender instanceof Player player)) {
            plugin.bundle().sendMessage(sender, "command.sender");
            return 0;
        }

        var character = (Character<?>) context.getArgument("character", Character.class);

        character.getEntity().map(Entity::getLocation).or(character::getSpawnLocation)
                .ifPresent(location -> player.teleportAsync(location, COMMAND, RETAIN_PASSENGERS));

        plugin.bundle().sendMessage(sender, "character.teleported.self",
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private int teleportPosition(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var resolver = context.getArgument("position", FinePositionResolver.class);
        var position = resolver.resolve(context.getSource()).toLocation(context.getSource().getLocation().getWorld());
        return teleport(context, position);
    }

    private int teleportEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var selector = context.getArgument("entity", EntitySelectorArgumentResolver.class);
        var entity = selector.resolve(context.getSource()).getFirst();
        return teleport(context, entity.getLocation());
    }

    private int teleport(CommandContext<CommandSourceStack> context, Location location) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);

        character.getEntity().ifPresent(entity -> entity.teleportAsync(location, COMMAND, RETAIN_PASSENGERS));
        var success = character.setSpawnLocation(location);
        var message = success ? "character.teleported" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("world", location.getWorld().getName()),
                Formatter.number("x", location.x()),
                Formatter.number("y", location.y()),
                Formatter.number("z", location.z()),
                Formatter.number("yaw", location.getYaw()),
                Formatter.number("pitch", location.getPitch()),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
