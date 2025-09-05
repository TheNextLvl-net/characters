package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.IntTag;
import net.thenextlvl.nbt.tag.Tag;
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
        return IntTag.of(color.asARGB());
    }
}
