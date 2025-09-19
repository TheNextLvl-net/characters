package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.math.Rotation;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TeleportCommand extends CharacterActionCommand<Location> {
    private TeleportCommand(CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().teleport(), "teleport");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new TeleportCommand(plugin);
        var position = Commands.argument("position", ArgumentTypes.finePosition()).executes(command);
        var rotation = Commands.argument("rotation", ArgumentTypes.rotation()).executes(command);
        var world = Commands.argument("world", ArgumentTypes.world()).executes(command);
        return command.create().then(position.then(rotation.then(world)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var position = context.getArgument("position", FinePositionResolver.class).resolve(context.getSource());
        var rotationResolver = tryGetArgument(context, "rotation", RotationResolver.class).orElse(null);

        var world = tryGetArgument(context, "world", World.class).orElseGet(() -> context.getSource().getLocation().getWorld());
        var rotation = rotationResolver != null ? rotationResolver.resolve(context.getSource()) : Rotation.rotation(0, 0);

        var location = position.toLocation(world).setRotation(rotation);
        return addAction(context, location);
    }
}
