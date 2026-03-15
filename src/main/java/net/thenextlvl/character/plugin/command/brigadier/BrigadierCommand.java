package net.thenextlvl.character.plugin.command.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public abstract class BrigadierCommand {
    protected final CharacterPlugin plugin;

    private final @Nullable String permission;
    private final String name;

    protected BrigadierCommand(final CharacterPlugin plugin, final String name, @Nullable final String permission) {
        this.plugin = plugin;
        this.permission = permission;
        this.name = name;
    }

    protected LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(name).requires(this::canUse);
    }

    protected boolean canUse(final CommandSourceStack source) {
        return permission == null || source.getSender().hasPermission(permission);
    }

    protected <T> Optional<T> tryGetArgument(final CommandContext<CommandSourceStack> context, final String name, final Class<T> type) {
        try {
            return Optional.of(context.getArgument(name, type));
        } catch (final IllegalArgumentException e) {
            if (e.getMessage().equals("No such argument '" + name + "' exists on this command"))
                return Optional.empty();
            throw e;
        }
    }
}
