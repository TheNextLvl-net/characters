package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
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
        var tag = CompoundTag.empty();
        tag.add("w", quaternion.w);
        tag.add("x", quaternion.x);
        tag.add("y", quaternion.y);
        tag.add("z", quaternion.z);
        return tag;
    }
}
