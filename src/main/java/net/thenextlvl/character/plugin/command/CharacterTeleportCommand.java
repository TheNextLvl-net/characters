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
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.math.Rotation;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND;

@NullMarked
final class CharacterTeleportCommand extends BrigadierCommand {
    private CharacterTeleportCommand(final CharacterPlugin plugin) {
        super(plugin, "teleport", "characters.command.teleport");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterTeleportCommand(plugin);
        final var rotation = rotationArgument().executes(command::teleportPosition);
        final var position = positionArgument().executes(command::teleportPosition);
        final var entity = entityArgument().executes(command::teleportEntity);
        final var teleport = characterArgument(plugin).executes(command::teleportSelf)
                .then(position.then(rotation))
                .then(entity);
        return command.create().then(teleport);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> entityArgument() {
        return Commands.argument("entity", ArgumentTypes.entity());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> positionArgument() {
        return Commands.argument("position", ArgumentTypes.finePosition(true));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> rotationArgument() {
        return Commands.argument("rotation", ArgumentTypes.rotation());
    }

    private int teleportSelf(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();

        if (!(sender instanceof final Player player)) {
            plugin.bundle().sendMessage(sender, "command.sender");
            return 0;
        }

        final var character = (Character<?>) context.getArgument("character", Character.class);

        character.getEntity().map(Entity::getLocation).or(character::getSpawnLocation)
                .ifPresent(location -> player.teleportAsync(location, COMMAND));

        plugin.bundle().sendMessage(sender, "character.teleported.self",
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private int teleportPosition(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var resolver = context.getArgument("position", FinePositionResolver.class);
        final var rotationResolver = tryGetArgument(context, "rotation", RotationResolver.class).orElse(null);
        final var rotation = rotationResolver != null ? rotationResolver.resolve(context.getSource()) : Rotation.rotation(0, 0);
        final var position = resolver.resolve(context.getSource()).toLocation(context.getSource().getLocation().getWorld());
        return teleport(context, position.setRotation(rotation));
    }

    private int teleportEntity(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var selector = context.getArgument("entity", EntitySelectorArgumentResolver.class);
        final var entity = selector.resolve(context.getSource()).getFirst();
        return teleport(context, entity.getLocation());
    }

    private int teleport(final CommandContext<CommandSourceStack> context, final Location location) {
        final var sender = context.getSource().getSender();
        final var character = (Character<?>) context.getArgument("character", Character.class);

        character.getEntity().ifPresent(entity -> entity.teleportAsync(location, COMMAND));
        final var success = character.setSpawnLocation(location);
        final var message = success ? "character.teleported" : "nothing.changed";

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
