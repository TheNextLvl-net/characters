package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import net.thenextlvl.character.notification.NotificationTrigger;
import net.thenextlvl.character.notification.NotificationTriggerRegistry;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class NotificationTriggerArgumentType implements CustomArgumentType.Converted<NotificationTrigger, String> {
    @Override
    public NotificationTrigger convert(final String nativeType) throws CommandSyntaxException {
        return NotificationTriggerRegistry.registry().getByName(nativeType).orElseThrow(() -> new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(Component.text("Unknown notification trigger: " + nativeType))
        ).create());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        NotificationTriggerRegistry.registry().getTriggers().stream()
                .map(NotificationTrigger::getName)
                .filter(name -> name.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
