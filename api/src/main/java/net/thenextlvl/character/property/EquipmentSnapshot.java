package net.thenextlvl.character.property;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a snapshot of an entity's equipment.
 *
 * @param items the items per equipment slot
 * @see EntityProperties#LIVING_ENTITY_EQUIPMENT
 * @since 0.9.0
 */
public record EquipmentSnapshot(@Unmodifiable Map<EquipmentSlot, Item> items) {
    /**
     * The equipment slots captured by a snapshot.
     *
     * @since 0.9.0
     */
    public static final @Unmodifiable Set<EquipmentSlot> SLOTS = Set.copyOf(EnumSet.of(
            EquipmentSlot.HAND, EquipmentSlot.OFF_HAND,
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    ));

    public EquipmentSnapshot {
        items = Map.copyOf(items);
    }

    /**
     * Creates a snapshot with every slot empty.
     *
     * @return an empty equipment snapshot
     * @since 0.9.0
     */
    @Contract(value = " -> new", pure = true)
    public static EquipmentSnapshot empty() {
        final var items = new EnumMap<EquipmentSlot, Item>(EquipmentSlot.class);
        SLOTS.forEach(slot -> items.put(slot, new Item(null, 0)));
        return new EquipmentSnapshot(items);
    }

    /**
     * Returns the item in the given slot.
     *
     * @param slot the equipment slot
     * @return the item in the given slot
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Optional<Item> getItem(final EquipmentSlot slot) {
        return Optional.ofNullable(items.get(slot));
    }

    /**
     * Creates a copy of this snapshot with the given item in the given slot.
     * <p>
     * The drop chance of the slot is retained.
     *
     * @param slot  the equipment slot
     * @param stack the item, or {@code null} to clear the slot
     * @return a new equipment snapshot
     * @since 0.9.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    public EquipmentSnapshot with(final EquipmentSlot slot, @Nullable final ItemStack stack) {
        final var items = new EnumMap<EquipmentSlot, Item>(EquipmentSlot.class);
        items.putAll(this.items);
        final var dropChance = getItem(slot).map(Item::dropChance).orElse(0f);
        items.put(slot, new Item(stack == null || stack.isEmpty() ? null : stack, dropChance));
        return new EquipmentSnapshot(items);
    }

    /**
     * Represents an item in an equipment slot.
     *
     * @param stack      the item stack, or {@code null} if the slot is empty
     * @param dropChance the chance of the item to drop
     * @since 0.9.0
     */
    public record Item(@Nullable ItemStack stack, float dropChance) {
        public Item {
            stack = stack == null ? null : stack.clone();
        }

        @Override
        public @Nullable ItemStack stack() {
            return stack == null ? null : stack.clone();
        }
    }
}
