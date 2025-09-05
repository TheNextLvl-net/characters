package net.thenextlvl.character.plugin.serialization;

import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NamedTextColorAdapter implements TagAdapter<NamedTextColor> {
    @Override
    public NamedTextColor deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return NamedTextColor.NAMES.valueOrThrow(tag.getAsString());
    }

    @Override
    public Tag serialize(NamedTextColor color, TagSerializationContext context) throws ParserException {
        return StringTag.of(color.toString());
    }
}
