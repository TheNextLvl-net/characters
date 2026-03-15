package net.thenextlvl.character.plugin.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.permissions.Permission;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class PermissionSuggestionProvider<T> implements SuggestionProvider<T> {
    private final CharacterPlugin plugin;

    public PermissionSuggestionProvider(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<T> context, final SuggestionsBuilder builder) {
        plugin.getServer().getPluginManager().getPermissions().stream()
                .map(Permission::getName)
                .filter(string -> string.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
