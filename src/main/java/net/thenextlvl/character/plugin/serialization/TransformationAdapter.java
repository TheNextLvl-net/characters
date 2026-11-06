package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TransformationAdapter implements TagAdapter<Transformation> {
    @Override
    public Transformation deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        return new Transformation(
                context.deserialize(root.get("translation"), Vector3f.class),
                context.deserialize(root.get("leftRotation"), Quaternionf.class),
                context.deserialize(root.get("scale"), Vector3f.class),
                context.deserialize(root.get("rightRotation"), Quaternionf.class)
        );
    }

    @Override
    public Tag serialize(final Transformation transformation, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("translation", context.serialize(transformation.getTranslation()))
                .put("leftRotation", context.serialize(transformation.getLeftRotation()))
                .put("scale", context.serialize(transformation.getScale()))
                .put("rightRotation", context.serialize(transformation.getRightRotation()))
                .build();
    }
}
