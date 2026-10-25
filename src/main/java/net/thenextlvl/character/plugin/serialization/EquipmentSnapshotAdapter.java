package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.property.EquipmentSnapshot;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.EnumMap;
import java.util.Map;

@NullMarked
public final class EquipmentSnapshotAdapter implements TagAdapter<EquipmentSnapshot> {
    private static final Map<EquipmentSlot, String> NAMES = Map.of(
            EquipmentSlot.HAND, "itemInMainHand",
            EquipmentSlot.OFF_HAND, "itemInOffHand",
            EquipmentSlot.HEAD, "helmet",
            EquipmentSlot.CHEST, "chestplate",
            EquipmentSlot.LEGS, "leggings",
            EquipmentSlot.FEET, "boots"
    );

    @Override
    public EquipmentSnapshot deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var items = new EnumMap<EquipmentSlot, EquipmentSnapshot.Item>(EquipmentSlot.class);
        NAMES.forEach((slot, name) -> {
            final var stack = root.optional(name).map(item -> context.deserialize(item, ItemStack.class)).orElse(null);
            final var dropChance = root.optional(name + "DropChance").map(Tag::getAsFloat).orElse(1f);
            items.put(slot, new EquipmentSnapshot.Item(stack, dropChance));
        });
        return new EquipmentSnapshot(items);
    }

    @Override
    public Tag serialize(final EquipmentSnapshot equipment, final TagSerializationContext context) throws ParserException {
        final var tag = CompoundTag.builder();
        equipment.items().forEach((slot, item) -> {
            final var name = NAMES.get(slot);
            if (name == null) return;
            final var stack = item.stack();
            if (stack != null && !stack.isEmpty()) tag.put(name, context.serialize(stack));
            tag.put(name + "DropChance", item.dropChance());
        });
        return tag.build();
    }
}
