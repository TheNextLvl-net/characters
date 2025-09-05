package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.EntityType;

public class PlayerCharacterArgument extends WrappedArgumentType<String, PlayerCharacter> {
    public PlayerCharacterArgument(CharacterPlugin plugin) {
        super(StringArgumentType.word(), (reader, type) -> {
            var character = plugin.characterController().getCharacter(type).orElse(null);
            if (character instanceof PlayerCharacter player) return player;
            throw new IllegalArgumentException("No player character was found");
        }, (context, builder) -> {
            plugin.characterController().getCharacters().stream()
                    .filter(character -> character.getType().equals(EntityType.PLAYER))
                    .map(Character::getName)
                    .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
