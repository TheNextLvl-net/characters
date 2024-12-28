package net.thenextlvl.character.plugin.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.character.plugin.CharacterPlugin;

import java.util.concurrent.CompletableFuture;

public class CharacterSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private final CharacterPlugin plugin;

    public CharacterSuggestionProvider(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        plugin.characterController().getCharacterNames().stream()
                .filter(name -> name.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
