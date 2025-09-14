package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Base64;

@NullMarked
public final class ItemStackAdapter implements TagAdapter<ItemStack> {
    @Override
    public ItemStack deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(tag.getAsString()));
    }

    @Override
    public Tag serialize(ItemStack itemStack, TagSerializationContext context) throws ParserException {
        return StringTag.of(Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));
    }
}
