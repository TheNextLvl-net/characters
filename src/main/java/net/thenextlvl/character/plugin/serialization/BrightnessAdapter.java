package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.bukkit.entity.Display.Brightness;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BrightnessAdapter implements TagAdapter<Brightness> {
    @Override
    public Brightness deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var blockLight = root.get("blockLight").getAsInt();
        var skyLight = root.get("skyLight").getAsInt();
        return new Brightness(blockLight, skyLight);
    }

    @Override
    public Tag serialize(Brightness brightness, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("blockLight", brightness.getBlockLight());
        tag.add("skyLight", brightness.getSkyLight());
        return tag;
    }
}
