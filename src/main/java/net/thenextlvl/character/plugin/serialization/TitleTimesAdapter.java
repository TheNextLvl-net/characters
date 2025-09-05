package net.thenextlvl.character.plugin.serialization;

import net.kyori.adventure.title.Title;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
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
        var tag = CompoundTag.empty();
        tag.add("fadeIn", times.fadeIn().toMillis());
        tag.add("stay", times.stay().toMillis());
        tag.add("fadeOut", times.fadeOut().toMillis());
        return tag;
    }
}
