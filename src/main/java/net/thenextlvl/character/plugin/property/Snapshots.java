package net.thenextlvl.character.plugin.property;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.thenextlvl.character.property.AttributesSnapshot;
import net.thenextlvl.character.property.EquipmentSnapshot;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Set;

@NullMarked
final class Snapshots {
    private Snapshots() {
    }

    public static AttributesSnapshot attributes(final Attributable attributable) {
        final var attributes = new HashMap<Attribute, AttributesSnapshot.Entry>();
        RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).forEach(attribute -> {
            final var instance = attributable.getAttribute(attribute);
            if (instance == null) return;
            attributes.put(attribute, new AttributesSnapshot.Entry(instance.getBaseValue(), Set.copyOf(instance.getModifiers())));
        });
        return new AttributesSnapshot(attributes);
    }

    public static void applyAttributes(final Attributable attributable, @Nullable final AttributesSnapshot snapshot) {
        if (snapshot == null) return;
        snapshot.attributes().forEach((attribute, entry) -> {
            final var instance = attributable.getAttribute(attribute);
            if (instance == null) return;
            instance.setBaseValue(entry.baseValue());
            entry.modifiers().forEach(modifier -> {
                instance.removeModifier(modifier.key());
                instance.addModifier(modifier);
            });
        });
    }

    public static @Nullable EquipmentSnapshot equipment(final LivingEntity entity) {
        final var equipment = entity.getEquipment();
        if (equipment == null) return null;
        final var items = new EnumMap<EquipmentSlot, EquipmentSnapshot.Item>(EquipmentSlot.class);
        EquipmentSnapshot.SLOTS.forEach(slot -> items.put(slot, new EquipmentSnapshot.Item(equipment.getItem(slot), equipment.getDropChance(slot))));
        return new EquipmentSnapshot(items);
    }

    public static void applyEquipment(final LivingEntity entity, @Nullable final EquipmentSnapshot snapshot) {
        final var equipment = entity.getEquipment();
        if (equipment == null || snapshot == null) return;
        snapshot.items().forEach((slot, item) -> {
            equipment.setItem(slot, item.stack());
            if (entity instanceof Mob) equipment.setDropChance(slot, item.dropChance());
        });
    }
}
