package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class QuaternionfAdapter implements TagAdapter<Quaternionf> {
    @Override
    public Quaternionf deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var w = root.get("w").getAsFloat();
        var x = root.get("x").getAsFloat();
        var y = root.get("y").getAsFloat();
        var z = root.get("z").getAsFloat();
        return new Quaternionf(x, y, z, w);
    }

    @Override
    public Tag serialize(Quaternionf quaternion, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("w", quaternion.w);
        tag.add("x", quaternion.x);
        tag.add("y", quaternion.y);
        tag.add("z", quaternion.z);
        return tag;
    }
}
