package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SendTitleCommand extends CharacterActionCommand<Title> {
    private SendTitleCommand(CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().sendTitle(), "send-title");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new SendTitleCommand(plugin);
        var title = Commands.argument("title", StringArgumentType.string()).executes(command);
        var subtitle = Commands.argument("subtitle", StringArgumentType.string()).executes(command);
        var times = command.titleTimesArgument();
        return command.create().then(title.then(subtitle.then(times)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> titleTimesArgument() {
        var fadeIn = Commands.argument("fade-in", ArgumentTypes.time());
        var stay = Commands.argument("stay", ArgumentTypes.time());
        var fadeOut = Commands.argument("fade-out", ArgumentTypes.time());
        return fadeIn.then(stay.then(fadeOut.executes(this)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var title = MiniMessage.miniMessage().deserialize(context.getArgument("title", String.class));
        var subtitle = tryGetArgument(context, "subtitle", String.class)
                .map(MiniMessage.miniMessage()::deserialize)
                .orElse(Component.empty());
        var fadeIn = tryGetArgument(context, "fade-in", int.class).map(Ticks::duration).orElse(null);
        var stay = tryGetArgument(context, "stay", int.class).map(Ticks::duration).orElse(null);
        var fadeOut = tryGetArgument(context, "fade-out", int.class).map(Ticks::duration).orElse(null);
        var times = fadeIn != null && stay != null && fadeOut != null ? Title.Times.times(fadeIn, stay, fadeOut) : null;
        return addAction(context, Title.title(title, subtitle, times));
    }
}
