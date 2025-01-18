package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.StringTag;
import core.nbt.tag.Tag;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class NamedTextColorAdapter implements TagAdapter<NamedTextColor> {
    @Override
    public NamedTextColor deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return Objects.requireNonNull(NamedTextColor.NAMES.value(tag.getAsString()), "Unknown color");
    }

    @Override
    public Tag serialize(NamedTextColor color, TagSerializationContext context) throws ParserException {
        return new StringTag(color.toString());
    }
}
