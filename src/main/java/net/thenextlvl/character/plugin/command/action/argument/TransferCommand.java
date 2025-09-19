package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

@NullMarked
public final class TransferCommand extends CharacterActionCommand<InetSocketAddress> {
    private TransferCommand(CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().transfer(), "transfer");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new TransferCommand(plugin);
        var hostname = Commands.argument("hostname", StringArgumentType.string()).executes(command);
        var port = Commands.argument("port", IntegerArgumentType.integer(1, 65535)).executes(command);
        return command.create().then(hostname.then(port));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var hostname = context.getArgument("hostname", String.class);
        var port = tryGetArgument(context, "port", int.class).orElse(25565);
        return addAction(context, new InetSocketAddress(hostname, port));
    }
}
