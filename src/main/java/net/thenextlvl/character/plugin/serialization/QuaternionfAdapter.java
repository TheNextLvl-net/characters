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
public final class QuaternionfAdapter implements TagAdapter<Quaternionf> {
    @Override
    public Quaternionf deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var w = root.get("w").getAsFloat();
        final var x = root.get("x").getAsFloat();
        final var y = root.get("y").getAsFloat();
        final var z = root.get("z").getAsFloat();
        return new Quaternionf(x, y, z, w);
    }

    @Override
    public Tag serialize(final Quaternionf quaternion, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("w", quaternion.w)
                .put("x", quaternion.x)
                .put("y", quaternion.y)
                .put("z", quaternion.z)
                .build();
    }
}
