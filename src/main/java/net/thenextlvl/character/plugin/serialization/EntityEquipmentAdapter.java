package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.plugin.model.PaperEntityEquipment;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityEquipmentAdapter implements TagAdapter<PaperEntityEquipment> {
    @Override
    public PaperEntityEquipment deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var itemInMainHand = root.optional("itemInMainHand").map(tag1 -> context.deserialize(tag1, ItemStack.class)).orElse(null);
        var itemInOffHand = root.optional("itemInOffHand").map(tag1 -> context.deserialize(tag1, ItemStack.class)).orElse(null);
        var helmet = root.optional("helmet").map(tag1 -> context.deserialize(tag1, ItemStack.class)).orElse(null);
        var chestplate = root.optional("chestplate").map(tag1 -> context.deserialize(tag1, ItemStack.class)).orElse(null);
        var leggings = root.optional("leggings").map(tag1 -> context.deserialize(tag1, ItemStack.class)).orElse(null);
        var boots = root.optional("boots").map(tag1 -> context.deserialize(tag1, ItemStack.class)).orElse(null);
        var itemInMainHandDropChance = root.optional("itemInMainHandDropChance").map(tag1 -> context.deserialize(tag1, float.class)).orElse(1f);
        var itemInOffHandDropChance = root.optional("itemInOffHandDropChance").map(tag1 -> context.deserialize(tag1, float.class)).orElse(1f);
        var helmetDropChance = root.optional("helmetDropChance").map(tag1 -> context.deserialize(tag1, float.class)).orElse(1f);
        var chestplateDropChance = root.optional("chestplateDropChance").map(tag1 -> context.deserialize(tag1, float.class)).orElse(1f);
        var leggingsDropChance = root.optional("leggingsDropChance").map(tag1 -> context.deserialize(tag1, float.class)).orElse(1f);
        var bootsDropChance = root.optional("bootsDropChance").map(tag1 -> context.deserialize(tag1, float.class)).orElse(1f);
        return new PaperEntityEquipment(itemInMainHand, itemInOffHand, helmet, chestplate, leggings, boots,
                itemInMainHandDropChance, itemInOffHandDropChance, helmetDropChance, chestplateDropChance, leggingsDropChance, bootsDropChance);
    }

    @Override
    public Tag serialize(PaperEntityEquipment equipment, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.empty();
        if (equipment.itemInMainHand != null && !equipment.itemInMainHand.isEmpty())
            tag.add("itemInMainHand", context.serialize(equipment.itemInMainHand));
        if (equipment.itemInOffHand != null && !equipment.itemInOffHand.isEmpty())
            tag.add("itemInOffHand", context.serialize(equipment.itemInOffHand));
        if (equipment.helmet != null && !equipment.helmet.isEmpty())
            tag.add("helmet", context.serialize(equipment.helmet));
        if (equipment.chestplate != null && !equipment.chestplate.isEmpty())
            tag.add("chestplate", context.serialize(equipment.chestplate));
        if (equipment.leggings != null && !equipment.leggings.isEmpty())
            tag.add("leggings", context.serialize(equipment.leggings));
        if (equipment.boots != null && !equipment.boots.isEmpty()) tag.add("boots", context.serialize(equipment.boots));
        tag.add("itemInMainHandDropChance", context.serialize(equipment.itemInMainHandDropChance));
        tag.add("itemInOffHandDropChance", context.serialize(equipment.itemInOffHandDropChance));
        tag.add("helmetDropChance", context.serialize(equipment.helmetDropChance));
        tag.add("chestplateDropChance", context.serialize(equipment.chestplateDropChance));
        tag.add("leggingsDropChance", context.serialize(equipment.leggingsDropChance));
        tag.add("bootsDropChance", context.serialize(equipment.bootsDropChance));
        return tag;
    }
}
