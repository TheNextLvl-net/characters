package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Vector3fAdapter implements TagAdapter<Vector3f> {
    @Override
    public Vector3f deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var x = root.get("x").getAsFloat();
        var y = root.get("y").getAsFloat();
        var z = root.get("z").getAsFloat();
        return new Vector3f(x, y, z);
    }

    @Override
    public Tag serialize(Vector3f vector, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.empty();
        tag.add("x", vector.x());
        tag.add("y", vector.y());
        tag.add("z", vector.z());
        return tag;
    }
}
