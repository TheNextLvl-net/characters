package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public class TitleAdapter implements TagAdapter<Title> {
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
        var tag = new CompoundTag();
        tag.add("title", context.serialize(title.title()));
        tag.add("subtitle", context.serialize(title.subtitle()));
        Optional.ofNullable(title.times()).ifPresent(times -> tag.add("times", context.serialize(times)));
        return tag;
    }
}
