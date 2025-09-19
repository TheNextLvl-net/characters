package net.thenextlvl.character.plugin.command.action;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.math.Rotation;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.plugin.model.ClickTypes;
import org.bukkit.EntityEffect;
import org.bukkit.Registry;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.time.Duration;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.action.CharacterActionCommand.actionArgument;

// todo: split up into multiple commands
@NullMarked
final class CharacterActionAddCommand extends BrigadierCommand {
    private CharacterActionAddCommand(CharacterPlugin plugin) {
        super(plugin, "add", "characters.command.action.add");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionAddCommand(plugin);
        var tree = command.create();
        for (var clickTypes : ClickTypes.values()) {
            var literal = clickTypes.name().toLowerCase();
            var chain = Commands.literal(literal)
                    .then(command.connect(clickTypes))
                    .then(command.playSound(clickTypes))
                    .then(command.runConsoleCommand(clickTypes))
                    .then(command.runPlayerCommand(clickTypes))
                    .then(command.sendActionBar(clickTypes))
                    .then(command.sendEntityEffect(clickTypes))
                    .then(command.sendMessage(clickTypes))
                    .then(command.teleport(clickTypes))
                    .then(command.title(clickTypes))
                    .then(command.transfer(clickTypes));
            tree.then(characterArgument(plugin).then(actionArgument(plugin).then(chain)));
        }
        return tree;
    }

    private ArgumentBuilder<CommandSourceStack, ?> title(ClickTypes clickTypes) {
        return Commands.literal("send-title")
                .then(Commands.argument("title", StringArgumentType.string())
                        .then(Commands.argument("subtitle", StringArgumentType.string())
                                .then(titleTimesArgument(clickTypes))
                                .executes(context -> {
                                    var subtitle = MiniMessage.miniMessage().deserialize(
                                            context.getArgument("subtitle", String.class));
                                    return title(context, subtitle, null, clickTypes);
                                }))
                        .executes(context -> title(context, Component.empty(), null, clickTypes)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> titleTimesArgument(ClickTypes clickTypes) {
        return Commands.argument("fade-in", ArgumentTypes.time())
                .then(Commands.argument("stay", ArgumentTypes.time())
                        .then(Commands.argument("fade-out", ArgumentTypes.time())
                                .executes(context -> title(context, clickTypes))));
    }

    private ArgumentBuilder<CommandSourceStack, ?> connect(ClickTypes clickTypes) {
        return Commands.literal("connect").then(stringArgument("server", ActionTypes.types().connect(), clickTypes));
    }

    private ArgumentBuilder<CommandSourceStack, ?> playSound(ClickTypes clickTypes) {
        return Commands.literal("play-sound").then(soundArgument(plugin)
                .then(soundSourceArgument(plugin)
                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0))
                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0, 2))
                                        .executes(context -> {
                                            var volume = context.getArgument("volume", float.class);
                                            var pitch = context.getArgument("pitch", float.class);
                                            return playSound(context, getSource(context), volume, pitch, clickTypes);
                                        }))
                                .executes(context -> {
                                    var volume = context.getArgument("volume", float.class);
                                    return playSound(context, getSource(context), volume, 1, clickTypes);
                                }))
                        .executes(context -> playSound(context, getSource(context), 1, 1, clickTypes)))
                .executes(context -> playSound(context, Sound.Source.MASTER, 1, 1, clickTypes)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> runConsoleCommand(ClickTypes clickTypes) {
        return Commands.literal("run-console-command").then(stringArgument("command", ActionTypes.types().runConsoleCommand(), clickTypes));
    }

    private ArgumentBuilder<CommandSourceStack, ?> runPlayerCommand(ClickTypes clickTypes) {
        return Commands.literal("run-command").then(stringArgument("command", ActionTypes.types().runCommand(), clickTypes));
    }

    private ArgumentBuilder<CommandSourceStack, ?> sendActionBar(ClickTypes clickTypes) {
        return Commands.literal("send-actionbar").then(stringArgument("message", ActionTypes.types().sendActionbar(), clickTypes));
    }

    private ArgumentBuilder<CommandSourceStack, ?> sendEntityEffect(ClickTypes clickTypes) {
        return Commands.literal("send-entity-effect").then(entityEffectArgument(plugin).executes(context -> {
            var entityEffect = context.getArgument("entity-effect", EntityEffect.class);
            return addAction(context, ActionTypes.types().sendEntityEffect(), entityEffect, clickTypes);
        }));
    }

    private ArgumentBuilder<CommandSourceStack, ?> sendMessage(ClickTypes clickTypes) {
        return Commands.literal("send-message").then(stringArgument("message", ActionTypes.types().sendMessage(), clickTypes));
    }

    private ArgumentBuilder<CommandSourceStack, ?> teleport(ClickTypes clickTypes) {
        return Commands.literal("teleport").then(positionArgument(plugin)
                .then(Commands.argument("rotation", ArgumentTypes.rotation())
                        .then(Commands.argument("world", ArgumentTypes.world())
                                .executes(context -> {
                                    var rotation = context.getArgument("rotation", RotationResolver.class);
                                    var world = context.getArgument("world", World.class);
                                    return teleport(context, rotation.resolve(context.getSource()), world, clickTypes);
                                }))
                        .executes(context -> {
                            var rotation = context.getArgument("rotation", RotationResolver.class);
                            var world = context.getSource().getLocation().getWorld();
                            return teleport(context, rotation.resolve(context.getSource()), world, clickTypes);
                        }))
                .executes(context -> {
                    var world = context.getSource().getLocation().getWorld();
                    return teleport(context, Rotation.rotation(0, 0), world, clickTypes);
                }));
    }

    private ArgumentBuilder<CommandSourceStack, ?> transfer(ClickTypes clickTypes) {
        return Commands.literal("transfer").then(Commands.argument("hostname", StringArgumentType.string())
                .then(Commands.argument("port", IntegerArgumentType.integer(1, 65535))
                        .executes(context -> {
                            var port = context.getArgument("port", int.class);
                            return transfer(context, port, clickTypes);
                        }))
                .executes(context -> transfer(context, 25565, clickTypes)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> stringArgument(String name, ActionType<String> actionType, ClickTypes clickTypes) {
        return Commands.argument(name, StringArgumentType.greedyString())
                .executes(context -> {
                    var string = context.getArgument(name, String.class);
                    return addAction(context, actionType, string, clickTypes);
                });
    }

    private int playSound(CommandContext<CommandSourceStack> context, Sound.Source source, float volume, float pitch, ClickTypes clickTypes) {
        var sender = context.getSource().getSender();
        var sound = context.getArgument("sound", org.bukkit.Sound.class);
        var key = Registry.SOUNDS.getKey(sound);
        if (key == null) throw new NullPointerException("Unknown sound");
        return addAction(context, ActionTypes.types().playSound(), Sound.sound(key, source, volume, pitch), clickTypes);
    }

    private int teleport(CommandContext<CommandSourceStack> context, Rotation rotation, World world, ClickTypes clickTypes) throws CommandSyntaxException {
        var resolver = context.getArgument("position", FinePositionResolver.class);
        var position = resolver.resolve(context.getSource());
        var location = position.toLocation(world).setRotation(rotation);
        return addAction(context, ActionTypes.types().teleport(), location, clickTypes);
    }

    private int title(CommandContext<CommandSourceStack> context, ClickTypes clickTypes) {
        var subtitle = MiniMessage.miniMessage().deserialize(
                context.getArgument("subtitle", String.class));
        var fadeIn = Ticks.duration(context.getArgument("fade-in", int.class));
        var stay = Ticks.duration(context.getArgument("stay", int.class));
        var fadeOut = Ticks.duration(context.getArgument("fade-out", int.class));
        var times = Title.Times.times(fadeIn, stay, fadeOut);
        return title(context, subtitle, times, clickTypes);
    }

    private int title(CommandContext<CommandSourceStack> context, Component subtitle, Title.@Nullable Times times, ClickTypes clickTypes) {
        var title = MiniMessage.miniMessage().deserialize(context.getArgument("title", String.class));
        return addAction(context, ActionTypes.types().sendTitle(), Title.title(title, subtitle, times), clickTypes);
    }

    private int transfer(CommandContext<CommandSourceStack> context, int port, ClickTypes clickTypes) {
        var hostname = context.getArgument("hostname", String.class);
        var address = new InetSocketAddress(hostname, port);
        return addAction(context, ActionTypes.types().transfer(), address, clickTypes);
    }

    private ArgumentBuilder<CommandSourceStack, ?> clickTypesArgument(CharacterPlugin plugin) {
        return Commands.argument("click-types", EnumArgumentType.of(ClickTypes.class, EnumStringCodec.lowerHyphen()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> entityEffectArgument(CharacterPlugin plugin) {
        return Commands.argument("entity-effect", EnumArgumentType.of(EntityEffect.class, EnumStringCodec.lowerHyphen()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> positionArgument(CharacterPlugin plugin) {
        return Commands.argument("position", ArgumentTypes.finePosition());
    }

    private ArgumentBuilder<CommandSourceStack, ?> soundArgument(CharacterPlugin plugin) {
        return Commands.argument("sound", ArgumentTypes.resource(RegistryKey.SOUND_EVENT));
    }

    private ArgumentBuilder<CommandSourceStack, ?> soundSourceArgument(CharacterPlugin plugin) {
        return Commands.argument("sound-source", EnumArgumentType.of(Sound.Source.class, EnumStringCodec.lowerHyphen()));
    }

    private Sound.Source getSource(CommandContext<CommandSourceStack> context) {
        return context.getArgument("sound-source", Sound.Source.class);
    }

    private <T> int addAction(CommandContext<CommandSourceStack> context, ActionType<T> actionType, T input, ClickTypes clickTypes) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);

        var previous = character.getAction(actionName);
        var cooldown = previous.map(ClickAction::getCooldown).orElse(Duration.ZERO);
        var permission = previous.map(ClickAction::getPermission).orElse(null);
        var chance = previous.map(ClickAction::getChance).orElse(100);

        var success = character.addAction(actionName, ClickAction.create(actionType, clickTypes.getClickTypes(), input, action -> {
            action.setChance(chance);
            action.setCooldown(cooldown);
            action.setPermission(permission);
        }));

        var message = success ? "character.action.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
