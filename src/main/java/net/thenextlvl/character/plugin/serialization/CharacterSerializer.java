package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.serialization.TagSerializer;
import core.nbt.tag.Tag;
import net.thenextlvl.character.Character;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterSerializer implements TagSerializer<Character<?>> {
    @Override
    public Tag serialize(Character<?> character, TagSerializationContext context) throws ParserException {
        return character.serialize();
    }
}
