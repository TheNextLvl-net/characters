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
    public Brightness deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var blockLight = root.get("blockLight").getAsInt();
        var skyLight = root.get("skyLight").getAsInt();
        return new Brightness(blockLight, skyLight);
    }

    @Override
    public Tag serialize(Brightness brightness, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.empty();
        tag.add("blockLight", brightness.getBlockLight());
        tag.add("skyLight", brightness.getSkyLight());
        return tag;
    }
}
