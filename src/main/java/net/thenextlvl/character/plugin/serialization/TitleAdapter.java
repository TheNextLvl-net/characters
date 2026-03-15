package net.thenextlvl.character.plugin.serialization;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public final class TitleAdapter implements TagAdapter<Title> {
    @Override
    public Title deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var title = context.deserialize(root.get("title"), Component.class);
        final var subtitle = context.deserialize(root.get("subtitle"), Component.class);
        final var times = root.optional("times").map(t -> context.deserialize(t, Title.Times.class)).orElse(null);
        return Title.title(title, subtitle, times);
    }

    @Override
    public Tag serialize(final Title title, final TagSerializationContext context) throws ParserException {
        final var tag = CompoundTag.builder();
        tag.put("title", context.serialize(title.title()));
        tag.put("subtitle", context.serialize(title.subtitle()));
        Optional.ofNullable(title.times()).ifPresent(times -> tag.put("times", context.serialize(times)));
        return tag.build();
    }
}
