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
import io.papermc.paper.command.brigadier.argument.RegistryArgumentExtractor;
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
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

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
        var chain = clickTypesArgument()
                .then(command.connect())
                .then(command.playSound())
                .then(command.runConsoleCommand())
                .then(command.runPlayerCommand())
                .then(command.sendActionBar())
                .then(command.sendEntityEffect())
                .then(command.sendMessage())
                .then(command.teleport())
                .then(command.title())
                .then(command.transfer());
        return tree.then(characterArgument(plugin).then(actionArgument(plugin).then(chain)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> clickTypesArgument() {
        return Commands.argument("click-types", EnumArgumentType.of(ClickTypes.class, EnumStringCodec.lowerHyphen()));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> entityEffectArgument() {
        return Commands.argument("entity-effect", EnumArgumentType.of(EntityEffect.class, EnumStringCodec.lowerHyphen()));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> positionArgument() {
        return Commands.argument("position", ArgumentTypes.finePosition());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> soundArgument() {
        return Commands.argument("sound", ArgumentTypes.resourceKey(RegistryKey.SOUND_EVENT));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> soundSourceArgument() {
        return Commands.argument("sound-source", EnumArgumentType.of(Sound.Source.class, EnumStringCodec.lowerHyphen()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> stringArgument(String name, ActionType<String> actionType) {
        return Commands.argument(name, StringArgumentType.greedyString()).executes(context -> {
            var string = context.getArgument(name, String.class);
            return addAction(context, actionType, string);
        });
    }

    private ArgumentBuilder<CommandSourceStack, ?> title() {
        return Commands.literal("send-title")
                .then(Commands.argument("title", StringArgumentType.string())
                        .then(Commands.argument("subtitle", StringArgumentType.string())
                                .then(titleTimesArgument())
                                .executes(this::title))
                        .executes(this::title));
    }

    private ArgumentBuilder<CommandSourceStack, ?> titleTimesArgument() {
        return Commands.argument("fade-in", ArgumentTypes.time())
                .then(Commands.argument("stay", ArgumentTypes.time())
                        .then(Commands.argument("fade-out", ArgumentTypes.time())
                                .executes(this::title)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> connect() {
        return Commands.literal("connect").then(stringArgument("server", ActionTypes.types().connect()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> playSound() {
        return Commands.literal("play-sound").then(soundArgument()
                .then(soundSourceArgument()
                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0))
                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0, 2))
                                        .executes(this::playSound))
                                .executes(this::playSound))
                        .executes(this::playSound))
                .executes(this::playSound));
    }

    private ArgumentBuilder<CommandSourceStack, ?> runConsoleCommand() {
        return Commands.literal("run-console-command").then(stringArgument("command", ActionTypes.types().runConsoleCommand()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> runPlayerCommand() {
        return Commands.literal("run-command").then(stringArgument("command", ActionTypes.types().runCommand()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> sendActionBar() {
        return Commands.literal("send-actionbar").then(stringArgument("message", ActionTypes.types().sendActionbar()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> sendEntityEffect() {
        return Commands.literal("send-entity-effect").then(entityEffectArgument().executes(context -> {
            var entityEffect = context.getArgument("entity-effect", EntityEffect.class);
            return addAction(context, ActionTypes.types().sendEntityEffect(), entityEffect);
        }));
    }

    private ArgumentBuilder<CommandSourceStack, ?> sendMessage() {
        return Commands.literal("send-message").then(stringArgument("message", ActionTypes.types().sendMessage()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> teleport() {
        return Commands.literal("teleport").then(positionArgument()
                .then(Commands.argument("rotation", ArgumentTypes.rotation())
                        .then(Commands.argument("world", ArgumentTypes.world())
                                .executes(this::teleport))
                        .executes(this::teleport))
                .executes(this::teleport));
    }

    private ArgumentBuilder<CommandSourceStack, ?> transfer() {
        return Commands.literal("transfer").then(Commands.argument("hostname", StringArgumentType.string())
                .then(Commands.argument("port", IntegerArgumentType.integer(1, 65535))
                        .executes(this::transfer))
                .executes(this::transfer));
    }

    private int teleport(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var position = context.getArgument("position", FinePositionResolver.class).resolve(context.getSource());
        var rotationResolver = tryGetArgument(context, "rotation", RotationResolver.class).orElse(null);

        var world = tryGetArgument(context, "world", World.class).orElseGet(() -> context.getSource().getLocation().getWorld());
        var rotation = rotationResolver != null ? rotationResolver.resolve(context.getSource()) : Rotation.rotation(0, 0);

        var location = position.toLocation(world).setRotation(rotation);
        return addAction(context, ActionTypes.types().teleport(), location);
    }

    private int title(CommandContext<CommandSourceStack> context) {
        var title = MiniMessage.miniMessage().deserialize(context.getArgument("title", String.class));
        var subtitle = tryGetArgument(context, "subtitle", String.class)
                .map(MiniMessage.miniMessage()::deserialize)
                .orElse(Component.empty());
        var fadeIn = tryGetArgument(context, "fade-in", int.class).map(Ticks::duration).orElse(null);
        var stay = tryGetArgument(context, "stay", int.class).map(Ticks::duration).orElse(null);
        var fadeOut = tryGetArgument(context, "fade-out", int.class).map(Ticks::duration).orElse(null);
        var times = fadeIn != null && stay != null && fadeOut != null ? Title.Times.times(fadeIn, stay, fadeOut) : null;
        return addAction(context, ActionTypes.types().sendTitle(), Title.title(title, subtitle, times));
    }

    private int transfer(CommandContext<CommandSourceStack> context) {
        var hostname = context.getArgument("hostname", String.class);
        var port = tryGetArgument(context, "port", int.class).orElse(25565);
        var address = new InetSocketAddress(hostname, port);
        return addAction(context, ActionTypes.types().transfer(), address);
    }

    private int playSound(CommandContext<CommandSourceStack> context) {
        var source = tryGetArgument(context, "source", Sound.Source.class).orElse(Sound.Source.MASTER);
        var volume = tryGetArgument(context, "volume", float.class).orElse(1f);
        var pitch = tryGetArgument(context, "pitch", float.class).orElse(1f);
        var sound = RegistryArgumentExtractor.getTypedKey(context, RegistryKey.SOUND_EVENT, "sound");
        return addAction(context, ActionTypes.types().playSound(), Sound.sound(sound, source, volume, pitch));
    }

    private <T> int addAction(CommandContext<CommandSourceStack> context, ActionType<T> actionType, T input) {
        var sender = context.getSource().getSender();
        var character = (Character<?>) context.getArgument("character", Character.class);
        var actionName = context.getArgument("action", String.class);
        var clickTypes = context.getArgument("click-types", ClickTypes.class);

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
