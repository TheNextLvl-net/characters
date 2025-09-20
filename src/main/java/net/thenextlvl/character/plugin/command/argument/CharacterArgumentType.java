package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class CharacterArgumentType implements CustomArgumentType.Converted<Character<?>, String> {
    private final CharacterPlugin plugin;

    public CharacterArgumentType(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Character<?> convert(String nativeType) {
        return plugin.characterController().getCharacter(nativeType)
                .orElseThrow(() -> new NullPointerException("No character was found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        plugin.characterController().getCharacterNames().stream()
                .map(StringArgumentType::escapeIfRequired)
                .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
