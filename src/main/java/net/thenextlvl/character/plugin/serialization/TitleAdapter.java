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
    public Title deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var title = context.deserialize(root.get("title"), Component.class);
        var subtitle = context.deserialize(root.get("subtitle"), Component.class);
        var times = root.optional("times").map(t -> context.deserialize(t, Title.Times.class)).orElse(null);
        return Title.title(title, subtitle, times);
    }

    @Override
    public Tag serialize(Title title, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.empty();
        tag.add("title", context.serialize(title.title()));
        tag.add("subtitle", context.serialize(title.subtitle()));
        Optional.ofNullable(title.times()).ifPresent(times -> tag.add("times", context.serialize(times)));
        return tag;
    }
}
