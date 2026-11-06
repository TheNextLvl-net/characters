package net.thenextlvl.character.plugin.command.action.notification;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import core.paper.brigadier.arguments.EnumArgumentType;
import core.paper.brigadier.arguments.codecs.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.RegistryArgumentExtractor;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.notification.Notification;
import net.thenextlvl.character.notification.NotificationTrigger;
import net.thenextlvl.character.notification.NotificationType;
import net.thenextlvl.character.notification.NotificationTypeRegistry;
import net.thenextlvl.character.notification.UnparsedTitle;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.NotificationTriggerArgumentType;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.action.CharacterActionCommand.actionArgument;

/**
 * Like the notifications of portals: messages, titles, action bar messages and sounds,
 * sent when an action succeeds, is on cooldown or the player lacks its permission.
 */
@NullMarked
public final class CharacterActionNotificationCommand extends BrigadierCommand {
    private CharacterActionNotificationCommand(final CharacterPlugin plugin) {
        super(plugin, "notification", "characters.command.action.notification");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterActionNotificationCommand(plugin);
        return command.create()
                .then(command.list())
                .then(command.remove())
                .then(command.set());
    }

    private ArgumentBuilder<CommandSourceStack, ?> action() {
        return characterArgument(plugin).suggests(new CharacterWithActionSuggestionProvider<>(plugin));
    }

    private ArgumentBuilder<CommandSourceStack, ?> trigger() {
        return Commands.argument("trigger", new NotificationTriggerArgumentType());
    }

    // set

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        final var sound = Commands.argument("sound", ArgumentTypes.resourceKey(RegistryKey.SOUND_EVENT)).executes(this::setSound);
        final var source = Commands.argument("sound-source", EnumArgumentType.of(Sound.Source.class, EnumStringCodec.lowerHyphen())).executes(this::setSound);
        final var volume = Commands.argument("volume", FloatArgumentType.floatArg(0)).executes(this::setSound);
        final var pitch = Commands.argument("pitch", FloatArgumentType.floatArg(0, 2)).executes(this::setSound);

        final var title = Commands.argument("title", StringArgumentType.string()).executes(this::setTitle);
        final var subtitle = Commands.argument("subtitle", StringArgumentType.string()).executes(this::setTitle);
        final var times = Commands.argument("fade-in", ArgumentTypes.time())
                .then(Commands.argument("stay", ArgumentTypes.time())
                        .then(Commands.argument("fade-out", ArgumentTypes.time()).executes(this::setTitle)));

        return Commands.literal("set").requires(source1 -> canUse(source1, "set"))
                .then(action().then(actionArgument(plugin).then(trigger()
                        .then(Commands.literal(NotificationType.sound().getName()).then(sound.then(source.then(volume.then(pitch)))))
                        .then(Commands.literal(NotificationType.title().getName()).then(title.then(subtitle.then(times))))
                        .then(Commands.literal(NotificationType.message().getName())
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(context -> set(context, NotificationType.message(), context.getArgument("message", String.class)))))
                        .then(Commands.literal(NotificationType.actionbar().getName())
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(context -> set(context, NotificationType.actionbar(), context.getArgument("message", String.class))))))));
    }

    private int setSound(final CommandContext<CommandSourceStack> context) {
        final var source = tryGetArgument(context, "sound-source", Sound.Source.class).orElse(Sound.Source.MASTER);
        final var volume = tryGetArgument(context, "volume", float.class).orElse(1f);
        final var pitch = tryGetArgument(context, "pitch", float.class).orElse(1f);
        final var sound = RegistryArgumentExtractor.getTypedKey(context, RegistryKey.SOUND_EVENT, "sound");
        return set(context, NotificationType.sound(), Sound.sound(sound, source, volume, pitch));
    }

    private int setTitle(final CommandContext<CommandSourceStack> context) {
        final var title = context.getArgument("title", String.class);
        final var subtitle = tryGetArgument(context, "subtitle", String.class).orElse("");
        final var fadeIn = tryGetArgument(context, "fade-in", int.class).map(Ticks::duration).orElse(null);
        final var stay = tryGetArgument(context, "stay", int.class).map(Ticks::duration).orElse(null);
        final var fadeOut = tryGetArgument(context, "fade-out", int.class).map(Ticks::duration).orElse(null);
        final var times = fadeIn != null && stay != null && fadeOut != null ? Title.Times.times(fadeIn, stay, fadeOut) : null;
        return set(context, NotificationType.title(), new UnparsedTitle(title, subtitle, times));
    }

    private <T> int set(final CommandContext<CommandSourceStack> context, final NotificationType<T> type, final T input) {
        final var action = action(context);
        if (action == null) return 0;
        final var trigger = context.getArgument("trigger", NotificationTrigger.class);
        final var success = action.getNotifications().set(trigger, type, input);
        sendMessage(context, success ? "character.action.notification.set" : "nothing.changed", trigger, type.getName());
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    // remove

    private ArgumentBuilder<CommandSourceStack, ?> remove() {
        final var type = Commands.argument("type", StringArgumentType.word()).suggests((context, builder) -> {
            NotificationTypeRegistry.registry().getNotificationTypes().stream()
                    .map(NotificationType::getName)
                    .filter(name -> name.contains(builder.getRemaining()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        }).executes(context -> {
            final var action = action(context);
            if (action == null) return 0;
            final var trigger = context.getArgument("trigger", NotificationTrigger.class);
            final var name = context.getArgument("type", String.class);
            final var success = NotificationTypeRegistry.registry().getByName(name)
                    .map(notificationType -> action.getNotifications().remove(trigger, notificationType))
                    .orElse(false);
            sendMessage(context, success ? "character.action.notification.removed" : "nothing.changed", trigger, name);
            return success ? Command.SINGLE_SUCCESS : 0;
        });
        return Commands.literal("remove").requires(source -> canUse(source, "remove"))
                .then(action().then(actionArgument(plugin).then(trigger().then(type))));
    }

    // list

    private ArgumentBuilder<CommandSourceStack, ?> list() {
        return Commands.literal("list").requires(source -> canUse(source, "list"))
                .then(action().then(actionArgument(plugin)
                        .then(trigger().executes(this::list))
                        .executes(this::list)));
    }

    private int list(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var action = action(context);
        if (action == null) return 0;
        final var character = context.getArgument("character", Character.class);
        final var actionName = context.getArgument("action", String.class);
        final var trigger = tryGetArgument(context, "trigger", NotificationTrigger.class).orElse(null);
        if (trigger != null) {
            list(sender, character, actionName, action, trigger);
            return Command.SINGLE_SUCCESS;
        }
        if (action.getNotifications().isEmpty()) {
            plugin.bundle().sendMessage(sender, "character.action.notification.list.empty",
                    Placeholder.unparsed("character", character.getName()),
                    Placeholder.unparsed("action", actionName));
            return 0;
        }
        action.getNotifications().stream().map(Notification::trigger).distinct()
                .forEach(trigger1 -> list(sender, character, actionName, action, trigger1));
        return Command.SINGLE_SUCCESS;
    }

    private void list(final CommandSender sender, final Character character, final String actionName,
                      final ClickAction<?> action, final NotificationTrigger trigger) {
        final var notifications = action.getNotifications().findByTrigger(trigger)
                .map(notification -> plugin.bundle().component("character.action.notification.list.entry", sender,
                        Placeholder.unparsed("type", notification.type().getName()),
                        Placeholder.unparsed("command", editCommand(character, actionName, notification))))
                .toList();
        final var message = notifications.isEmpty()
                ? "character.action.notification.list.empty.trigger"
                : "character.action.notification.list.trigger";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("trigger", trigger.getName()),
                Formatter.number("count", notifications.size()),
                Formatter.joining("notifications", notifications));
    }

    private static String editCommand(final Character character, final String actionName, final Notification<?> notification) {
        return "/character action notification set " + character.getName() + " " + actionName + " "
               + notification.trigger().getName() + " " + notification.type().getName() + " " + toString(notification.input());
    }

    private static String toString(final Object input) {
        return switch (input) {
            case final UnparsedTitle title -> {
                final var string = StringArgumentType.escapeIfRequired(title.title()) + " "
                                   + StringArgumentType.escapeIfRequired(title.subtitle());
                final var times = title.times();
                if (times == null) yield string;
                yield string + " " + times.fadeIn().toMillis() / 1000d + "s "
                      + times.stay().toMillis() / 1000d + "s "
                      + times.fadeOut().toMillis() / 1000d + "s";
            }
            case final Sound sound -> sound.name().asString() + " " + sound.source().name().toLowerCase()
                                      + " " + sound.volume() + " " + sound.pitch();
            default -> String.valueOf(input);
        };
    }

    // helpers

    private @Nullable ClickAction<?> action(final CommandContext<CommandSourceStack> context) {
        final var character = context.getArgument("character", Character.class);
        final var actionName = context.getArgument("action", String.class);
        final var action = character.getAction(actionName).orElse(null);
        if (action == null) plugin.bundle().sendMessage(context.getSource().getSender(), "character.action.not_found",
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("action", actionName));
        return action;
    }

    private void sendMessage(final CommandContext<CommandSourceStack> context, final String message,
                             final NotificationTrigger trigger, final String type) {
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", context.getArgument("character", Character.class).getName()),
                Placeholder.unparsed("action", context.getArgument("action", String.class)),
                Placeholder.unparsed("trigger", trigger.getName()),
                Placeholder.unparsed("type", type));
    }

    private boolean canUse(final CommandSourceStack source, final String subcommand) {
        return source.getSender().hasPermission("characters.command.action.notification." + subcommand);
    }
}
