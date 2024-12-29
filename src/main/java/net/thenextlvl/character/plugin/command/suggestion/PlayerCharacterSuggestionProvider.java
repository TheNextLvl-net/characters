package net.thenextlvl.character.plugin.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class PlayerCharacterSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private final CharacterPlugin plugin;

    public PlayerCharacterSuggestionProvider(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        plugin.characterController().getCharacters().stream()
                .filter(character -> character.getType().equals(EntityType.PLAYER))
                .map(Character::getName)
                .filter(name -> name.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
