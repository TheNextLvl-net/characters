package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.action.ClickTypes;
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.nameArgument;

@NullMarked
class CharacterActionAddCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("add").then(characterArgument(plugin)
                .then(clickTypesArgument(plugin).then(nameArgument(plugin)
                        .then(connect(plugin))
                        .then(runConsoleCommand(plugin))
                        .then(runPlayerCommand(plugin))
                        .then(runPlayerCommandPermitted(plugin))
                        .then(sendActionBar(plugin))
                        .then(sendMessage(plugin))
                        .then(transfer(plugin)))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> connect(CharacterPlugin plugin) {
        return Commands.literal("connect").then(stringArgument(plugin, "server", plugin.connect));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> runConsoleCommand(CharacterPlugin plugin) {
        return Commands.literal("run-console-command").then(stringArgument(plugin, "command", plugin.runConsoleCommand));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> runPlayerCommand(CharacterPlugin plugin) {
        return Commands.literal("run-player-command").then(stringArgument(plugin, "command", plugin.runPlayerCommand));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> runPlayerCommandPermitted(CharacterPlugin plugin) {
        return Commands.literal("run-player-command-permitted").then(stringArgument(plugin, "command", plugin.runPlayerCommandPermitted));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> sendActionBar(CharacterPlugin plugin) {
        return Commands.literal("send-actionbar").then(messageArgument(plugin, plugin.sendActionbar));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> sendMessage(CharacterPlugin plugin) {
        return Commands.literal("send-message").then(messageArgument(plugin, plugin.sendMessage));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> transfer(CharacterPlugin plugin) {
        return Commands.literal("transfer").then(Commands.argument("hostname", StringArgumentType.string())
                .then(Commands.argument("port", IntegerArgumentType.integer(1, 65535))
                        .executes(context -> {
                            var port = context.getArgument("port", int.class);
                            return transfer(context, port, plugin);
                        }))
                .executes(context -> transfer(context, 25565, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> messageArgument(CharacterPlugin plugin, ActionType<Component> actionType) {
        return Commands.argument("message", StringArgumentType.greedyString())
                .executes(context -> {
                    var message = MiniMessage.miniMessage().deserialize(context.getArgument("message", String.class));
                    return addAction(context, actionType, message, plugin);
                });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> stringArgument(CharacterPlugin plugin, String name, ActionType<String> actionType) {
        return Commands.argument(name, StringArgumentType.greedyString())
                .executes(context -> {
                    var string = context.getArgument(name, String.class);
                    return addAction(context, actionType, string, plugin);
                });
    }

    private static int transfer(CommandContext<CommandSourceStack> context, int port, CharacterPlugin plugin) {
        var hostname = context.getArgument("hostname", String.class);
        var address = new InetSocketAddress(hostname, port);
        return addAction(context, plugin.transfer, address, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> clickTypesArgument(CharacterPlugin plugin) {
        return Commands.argument("click-types", new EnumArgument<>(ClickTypes.class));
    }

    private static <T> int addAction(CommandContext<CommandSourceStack> context, ActionType<T> actionType, T input, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("character", String.class);
        var character = plugin.characterController().getCharacter(name).orElse(null);
        if (character == null) {
            plugin.bundle().sendMessage(sender, "character.not_found", Placeholder.unparsed("name", name));
            return 0;
        }
        var clickTypes = context.getArgument("click-types", ClickTypes.class);
        var actionName = context.getArgument("name", String.class);

        var clickAction = new ClickAction<>(actionName, actionType, clickTypes.getClickTypes(), input);
        var success = character.addAction(clickAction);

        var message = success ? "character.action.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", name));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
