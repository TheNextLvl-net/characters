package net.thenextlvl.character.plugin.serialization;

import net.kyori.adventure.title.Title;
import net.thenextlvl.character.notification.UnparsedTitle;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class UnparsedTitleAdapter implements TagAdapter<UnparsedTitle> {
    @Override
    public UnparsedTitle deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var title = root.get("title").getAsString();
        final var subtitle = root.get("subtitle").getAsString();
        final var times = root.optional("times").map(times1 -> context.deserialize(times1, Title.Times.class)).orElse(null);
        return new UnparsedTitle(title, subtitle, times);
    }

    @Override
    public Tag serialize(final UnparsedTitle title, final TagSerializationContext context) throws ParserException {
        final var tag = CompoundTag.builder()
                .put("title", title.title())
                .put("subtitle", title.subtitle());
        if (title.times() != null) tag.put("times", context.serialize(title.times()));
        return tag.build();
    }
}
