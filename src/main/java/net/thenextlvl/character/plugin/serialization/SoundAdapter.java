package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SoundAdapter implements TagAdapter<Sound> {
    @Override
    public Sound deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var name = context.deserialize(root.get("name"), Key.class);
        var source = Sound.Source.valueOf(root.get("source").getAsString());
        var volume = root.get("volume").getAsFloat();
        var pitch = root.get("pitch").getAsFloat();
        return Sound.sound(name, source, volume, pitch);
    }

    @Override
    public Tag serialize(Sound sound, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("name", context.serialize(sound.name()));
        tag.add("source", sound.source().name());
        tag.add("volume", sound.volume());
        tag.add("pitch", sound.pitch());
        return tag;
    }
}
