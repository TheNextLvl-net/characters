package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.entity.Display.Brightness;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BrightnessAdapter implements TagAdapter<Brightness> {
    @Override
    public Brightness deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var blockLight = root.get("blockLight").getAsInt();
        final var skyLight = root.get("skyLight").getAsInt();
        return new Brightness(blockLight, skyLight);
    }

    @Override
    public Tag serialize(final Brightness brightness, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("blockLight", brightness.getBlockLight())
                .put("skyLight", brightness.getSkyLight())
                .build();
    }
}
