package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.LongTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
public class DurationAdapter implements TagAdapter<Duration> {
    @Override
    public Duration deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return Duration.ofMillis(tag.getAsLong());
    }

    @Override
    public Tag serialize(Duration duration, TagSerializationContext context) throws ParserException {
        return new LongTag(duration.toMillis());
    }
}
