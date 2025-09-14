package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class PlayerCharacterArgumentType implements CustomArgumentType.Converted<PlayerCharacter, String> {
    private final CharacterPlugin plugin;

    public PlayerCharacterArgumentType(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public PlayerCharacter convert(String nativeType) {
        var character = plugin.characterController().getCharacter(nativeType).orElse(null);
        if (character instanceof PlayerCharacter player) return player;
        throw new IllegalArgumentException("No player character was found");
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        plugin.characterController().getCharacters().stream()
                .filter(character -> character.getType().equals(EntityType.PLAYER))
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
