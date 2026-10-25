package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class MannequinCharacterArgumentType implements CustomArgumentType.Converted<Character, String> {
    private final CharacterPlugin plugin;

    public MannequinCharacterArgumentType(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Character convert(final String nativeType) {
        return plugin.characterProvider().getCharacter(nativeType)
                .filter(character -> character.getType().equals(EntityType.MANNEQUIN))
                .orElseThrow(() -> new IllegalArgumentException("No player character was found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        plugin.characterProvider().getCharacters()
                .filter(character -> character.getType().equals(EntityType.MANNEQUIN))
                .map(Character::getName)
                .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
