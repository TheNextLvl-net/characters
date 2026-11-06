package net.thenextlvl.character.plugin.command.action;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.suggestion.CharacterWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.action.CharacterActionCommand.actionArgument;

@NullMarked
final class CharacterActionCooldownCommand extends ActionCommand {
    private CharacterActionCooldownCommand(final CharacterPlugin plugin) {
        super(plugin, "cooldown", "characters.command.action.cooldown");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterActionCooldownCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .suggests(new CharacterWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin)
                        .then(cooldownArgument().executes(command))
                        .then(command.message())
                        .then(command.sound())
                        .executes(command)));
    }

    // the message shown while the action is on cooldown, without one it is removed
    private ArgumentBuilder<CommandSourceStack, ?> message() {
        return Commands.literal("message")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> setMessage(context, context.getArgument("message", String.class))))
                .executes(context -> setMessage(context, null));
    }

    // the sound played while the action is on cooldown, without one it is removed
    private ArgumentBuilder<CommandSourceStack, ?> sound() {
        return Commands.literal("sound")
                .then(Commands.argument("sound", ArgumentTypes.resourceKey(RegistryKey.SOUND_EVENT))
                        .executes(context -> {
                            @SuppressWarnings("unchecked") final var key = (TypedKey<org.bukkit.Sound>) context.getArgument("sound", TypedKey.class);
                            return setSound(context, Sound.sound(key.key(), Sound.Source.NEUTRAL, 1, 1));
                        }))
                .executes(context -> setSound(context, null));
    }

    private int setMessage(final CommandContext<CommandSourceStack> context, @Nullable final String message) {
        final var character = context.getArgument("character", Character.class);
        final var actionName = context.getArgument("action", String.class);
        final var action = character.getAction(actionName).orElse(null);
        if (action == null) return 0;
        final var success = action.setCooldownMessage(message);
        final var feedback = !success ? "nothing.changed" : message != null
                ? "character.action.cooldown.message.set" : "character.action.cooldown.message.removed";
        plugin.bundle().sendMessage(context.getSource().getSender(), feedback,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("message", message != null ? message : ""));
        return success ? SINGLE_SUCCESS : 0;
    }

    private int setSound(final CommandContext<CommandSourceStack> context, @Nullable final Sound sound) {
        final var character = context.getArgument("character", Character.class);
        final var actionName = context.getArgument("action", String.class);
        final var action = character.getAction(actionName).orElse(null);
        if (action == null) return 0;
        final var success = action.setCooldownSound(sound);
        final var feedback = !success ? "nothing.changed" : sound != null
                ? "character.action.cooldown.sound.set" : "character.action.cooldown.sound.removed";
        plugin.bundle().sendMessage(context.getSource().getSender(), feedback,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("sound", sound != null ? sound.name().asString() : ""));
        return success ? SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> cooldownArgument() {
        return Commands.argument("cooldown", ArgumentTypes.time());
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final Character character, final ClickAction<?> action, final String actionName) {
        final var cooldown = tryGetArgument(context, "cooldown", int.class).map(Tick::of).orElse(null);
        final var success = cooldown != null && action.setCooldown(cooldown);
        final var message = cooldown == null ? "character.action.cooldown"
                : success ? cooldown.isZero() ? "character.action.cooldown.removed"
                : "character.action.cooldown.set"
                : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("character", character.getName()),
                Formatter.number("cooldown", (cooldown != null ? cooldown : action.getCooldown()).toMillis() / 1000d));
        return success ? SINGLE_SUCCESS : 0;
    }
}
