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
public final class TitleTimesAdapter implements TagAdapter<Title.Times> {
    @Override
    public Title.Times deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var fadeIn = Duration.ofMillis(root.get("fadeIn").getAsLong());
        final var stay = Duration.ofMillis(root.get("stay").getAsLong());
        final var fadeOut = Duration.ofMillis(root.get("fadeOut").getAsLong());
        return Title.Times.times(fadeIn, stay, fadeOut);
    }

    @Override
    public Tag serialize(final Title.Times times, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("fadeIn", times.fadeIn().toMillis())
                .put("stay", times.stay().toMillis())
                .put("fadeOut", times.fadeOut().toMillis())
                .build();
    }
}
