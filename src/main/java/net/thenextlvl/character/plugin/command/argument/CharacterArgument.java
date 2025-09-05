package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;

public class CharacterArgument extends WrappedArgumentType<String, Character<?>> {
    public CharacterArgument(CharacterPlugin plugin) {
        super(StringArgumentType.word(), (reader, type) -> plugin.characterController().getCharacter(type)
                .orElseThrow(() -> new NullPointerException("No character was found")), (context, builder) -> {
            plugin.characterController().getCharacterNames().stream()
                    .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
