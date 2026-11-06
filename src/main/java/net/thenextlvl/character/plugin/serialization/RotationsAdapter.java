package net.thenextlvl.character.plugin.serialization;

import io.papermc.paper.math.Rotations;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RotationsAdapter implements TagAdapter<Rotations> {
    @Override
    public Rotations deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var x = root.get("x").getAsDouble();
        final var y = root.get("y").getAsDouble();
        final var z = root.get("z").getAsDouble();
        return Rotations.ofDegrees(x, y, z);
    }

    @Override
    public Tag serialize(final Rotations rotations, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("x", rotations.x())
                .put("y", rotations.y())
                .put("z", rotations.z())
                .build();
    }
}
