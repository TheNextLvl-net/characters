package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.IntTag;
import core.nbt.tag.Tag;
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ColorAdapter implements TagAdapter<Color> {
    @Override
    public Color deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return Color.fromARGB(tag.getAsInt());
    }

    @Override
    public Tag serialize(Color color, TagSerializationContext context) throws ParserException {
        return new IntTag(color.asARGB());
    }
}
