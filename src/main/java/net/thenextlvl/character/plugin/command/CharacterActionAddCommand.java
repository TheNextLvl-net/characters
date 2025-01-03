package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.action.ClickTypes;
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import org.bukkit.Registry;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

import static net.thenextlvl.character.plugin.command.CharacterActionCommand.actionArgument;
import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterActionAddCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("add").then(characterArgument(plugin)
                .then(clickTypesArgument(plugin).then(actionArgument(plugin)
                        .then(connect(plugin))
                        .then(playSound(plugin))
                        .then(runConsoleCommand(plugin))
                        .then(runPlayerCommand(plugin))
                        .then(sendActionBar(plugin))
                        .then(sendMessage(plugin))
                        .then(teleport(plugin))
                        .then(transfer(plugin)))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> connect(CharacterPlugin plugin) {
        return Commands.literal("connect").then(stringArgument(plugin, "server", plugin.connect));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> playSound(CharacterPlugin plugin) {
        return Commands.literal("play-sound").then(soundArgument(plugin)
                .then(soundSourceArgument(plugin)
                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0))
                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0, 2))
                                        .executes(context -> {
                                            var volume = context.getArgument("volume", float.class);
                                            var pitch = context.getArgument("pitch", float.class);
                                            return playSound(context, getSource(context), volume, pitch, plugin);
                                        }))
                                .executes(context -> {
                                    var volume = context.getArgument("volume", float.class);
                                    return playSound(context, getSource(context), volume, 1, plugin);
                                }))
                        .executes(context -> playSound(context, getSource(context), 1, 1, plugin)))
                .executes(context -> playSound(context, Sound.Source.MASTER, 1, 1, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> runConsoleCommand(CharacterPlugin plugin) {
        return Commands.literal("run-console-command").then(stringArgument(plugin, "command", plugin.runConsoleCommand));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> runPlayerCommand(CharacterPlugin plugin) {
        return Commands.literal("run-command").then(stringArgument(plugin, "command", plugin.runCommand));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> sendActionBar(CharacterPlugin plugin) {
        return Commands.literal("send-actionbar").then(messageArgument(plugin, plugin.sendActionbar));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> sendMessage(CharacterPlugin plugin) {
        return Commands.literal("send-message").then(messageArgument(plugin, plugin.sendMessage));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teleport(CharacterPlugin plugin) {
        return Commands.literal("teleport").then(positionArgument(plugin)
                .then(Commands.argument("yaw", FloatArgumentType.floatArg(-180, 180))
                        .then(Commands.argument("pitch", FloatArgumentType.floatArg(-90, 90))
                                .then(Commands.argument("world", ArgumentTypes.world())
                                        .executes(context -> {
                                            var yaw = context.getArgument("yaw", float.class);
                                            var pitch = context.getArgument("pitch", float.class);
                                            var world = context.getArgument("world", World.class);
                                            return teleport(context, yaw, pitch, world, plugin);
                                        }))
                                .executes(context -> {
                                    var yaw = context.getArgument("yaw", float.class);
                                    var pitch = context.getArgument("pitch", float.class);
                                    var world = context.getSource().getLocation().getWorld();
                                    return teleport(context, yaw, pitch, world, plugin);
                                })))
                .executes(context -> {
                    var world = context.getSource().getLocation().getWorld();
                    return teleport(context, 0, 0, world, plugin);
                }));
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

    private static int playSound(CommandContext<CommandSourceStack> context, Sound.Source source, float volume, float pitch, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var sound = context.getArgument("sound", org.bukkit.Sound.class);
        var key = Registry.SOUNDS.getKey(sound);
        if (key == null) throw new NullPointerException("Unknown sound");
        return addAction(context, plugin.playSound, Sound.sound(key, source, volume, pitch), plugin);
    }

    private static int teleport(CommandContext<CommandSourceStack> context, float yaw, float pitch, World world, CharacterPlugin plugin) throws CommandSyntaxException {
        var resolver = context.getArgument("position", FinePositionResolver.class);
        var position = resolver.resolve(context.getSource());
        var location = position.toLocation(world);
        location.setYaw(yaw);
        location.setPitch(pitch);
        return addAction(context, plugin.teleport, location, plugin);
    }

    private static int transfer(CommandContext<CommandSourceStack> context, int port, CharacterPlugin plugin) {
        var hostname = context.getArgument("hostname", String.class);
        var address = new InetSocketAddress(hostname, port);
        return addAction(context, plugin.transfer, address, plugin);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> clickTypesArgument(CharacterPlugin plugin) {
        return Commands.argument("click-types", new EnumArgument<>(ClickTypes.class));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> positionArgument(CharacterPlugin plugin) {
        return Commands.argument("position", ArgumentTypes.finePosition());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> soundArgument(CharacterPlugin plugin) {
        return Commands.argument("sound", ArgumentTypes.resource(RegistryKey.SOUND_EVENT));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> soundSourceArgument(CharacterPlugin plugin) {
        return Commands.argument("sound-source", new EnumArgument<>(Sound.Source.class));
    }

    private static Sound.Source getSource(CommandContext<CommandSourceStack> context) {
        return context.getArgument("sound-source", Sound.Source.class);
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
        var actionName = context.getArgument("action", String.class);

        var action = new ClickAction<>(actionType, clickTypes.getClickTypes(), input);
        var success = character.addAction(actionName, action);

        var message = success ? "character.action.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", name));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
