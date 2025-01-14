package net.thenextlvl.character.plugin.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;

import java.util.concurrent.CompletableFuture;

public class CharacterWithActionSuggestionProvider<T> implements SuggestionProvider<T> {
    private final CharacterPlugin plugin;

    public CharacterWithActionSuggestionProvider(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<T> context, SuggestionsBuilder builder) {
            plugin.characterController().getCharacters().stream()
                    .filter(character -> !character.getActions().isEmpty())
                    .map(Character::getName)
                    .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
    }
}
