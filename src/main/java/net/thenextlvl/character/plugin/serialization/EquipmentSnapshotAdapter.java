package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.property.EquipmentSnapshot;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagDeserializer;
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
    @Override
    public EquipmentSnapshot deserialize(final Tag root, final TagDeserializationContext context) {
        try {
            final var items = new EnumMap<EquipmentSlot, EquipmentSnapshot.Item>(EquipmentSlot.class);
            root.getAsCompound().forEach((name, tag) -> {
                final var slot = EquipmentSlot.valueOf(name);
                final var compound = tag.getAsCompound();
                final var stack = compound.optional("item").map(item -> context.deserialize(item, ItemStack.class)).orElse(null);
                final var dropChance = compound.optional("dropChance").map(Tag::getAsFloat).orElse(1f);
                items.put(slot, new EquipmentSnapshot.Item(stack, dropChance));
            });
            return new EquipmentSnapshot(items);
        } catch (final Exception e) {
            final class Holder {
                private static final Legacy INSTANCE = new Legacy();
            }
            try {
                return Holder.INSTANCE.deserialize(root, context);
            } catch (final Exception ignored) {
                throw new ParserException("Failed to deserialize equipment snapshot", e);
            }
        }
    }

    @Override
    public Tag serialize(final EquipmentSnapshot equipment, final TagSerializationContext context) {
        final var tag = CompoundTag.builder();
        equipment.items().forEach((slot, item) -> {
            final var root = CompoundTag.builder();
            final var stack = item.stack();
            if (stack != null && !stack.isEmpty()) root.put("item", context.serialize(stack));
            root.put("dropChance", item.dropChance());
            tag.put(slot.name(), root.build());
        });
        return tag.build();
    }

    private static final class Legacy implements TagDeserializer<EquipmentSnapshot> {
        private static final Map<EquipmentSlot, String> NAMES = Map.of(
                EquipmentSlot.HAND, "itemInMainHand",
                EquipmentSlot.OFF_HAND, "itemInOffHand",
                EquipmentSlot.HEAD, "helmet",
                EquipmentSlot.CHEST, "chestplate",
                EquipmentSlot.LEGS, "leggings",
                EquipmentSlot.FEET, "boots",
                EquipmentSlot.BODY, "body",
                EquipmentSlot.SADDLE, "saddle"
        );

        @Override
        public EquipmentSnapshot deserialize(final Tag tag, final TagDeserializationContext context) {
            final var root = tag.getAsCompound();
            final var items = new EnumMap<EquipmentSlot, EquipmentSnapshot.Item>(EquipmentSlot.class);
            NAMES.forEach((slot, name) -> {
                final var stack = root.optional(name).map(item -> context.deserialize(item, ItemStack.class)).orElse(null);
                final var dropChance = root.optional(name + "DropChance").map(Tag::getAsFloat).orElse(1f);
                items.put(slot, new EquipmentSnapshot.Item(stack, dropChance));
            });
            return new EquipmentSnapshot(items);
        }
    }
}
