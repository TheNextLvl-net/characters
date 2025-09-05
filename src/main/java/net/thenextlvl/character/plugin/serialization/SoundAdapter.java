package net.thenextlvl.character.plugin.serialization;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
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
        var tag = CompoundTag.empty();
        tag.add("name", context.serialize(sound.name()));
        tag.add("source", sound.source().name());
        tag.add("volume", sound.volume());
        tag.add("pitch", sound.pitch());
        return tag;
    }
}
