package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import net.kyori.adventure.title.Title;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
public class TitleTimesAdapter implements TagAdapter<Title.Times> {
    @Override
    public Title.Times deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var fadeIn = Duration.ofMillis(root.get("fadeIn").getAsLong());
        var stay = Duration.ofMillis(root.get("stay").getAsLong());
        var fadeOut = Duration.ofMillis(root.get("fadeOut").getAsLong());
        return Title.Times.times(fadeIn, stay, fadeOut);
    }

    @Override
    public Tag serialize(Title.Times times, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("fadeIn", times.fadeIn().toMillis());
        tag.add("stay", times.stay().toMillis());
        tag.add("fadeOut", times.fadeOut().toMillis());
        return tag;
    }
}
