package net.thenextlvl.character.plugin.serialization;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.thenextlvl.character.property.AttributesSnapshot;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NullMarked
public final class AttributesSnapshotAdapter implements TagAdapter<AttributesSnapshot> {
    @Override
    @SuppressWarnings("PatternValidation")
    public AttributesSnapshot deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE);
        final var attributes = new HashMap<Attribute, AttributesSnapshot.Entry>();
        tag.getAsCompound().forEach((key, value) -> {
            final var attribute = registry.get(Key.key(key));
            if (attribute == null) return;
            final var root = value.getAsCompound();
            final var modifiers = new HashSet<AttributeModifier>();
            root.optional("modifiers").map(Tag::getAsCompound).ifPresent(tags -> tags.forEach((name, modifierTag) -> {
                final var modifierKey = NamespacedKey.fromString(name);
                if (modifierKey == null) return;
                final var modifier = modifierTag.getAsCompound();
                final var amount = modifier.get("amount").getAsDouble();
                final var operation = context.deserialize(modifier.get("operation"), AttributeModifier.Operation.class);
                final var slot = modifier.optional("slot")
                        .map(slotTag -> context.deserialize(slotTag, EquipmentSlotGroup.class))
                        .orElse(EquipmentSlotGroup.ANY);
                modifiers.add(new AttributeModifier(modifierKey, amount, operation, slot));
            }));
            attributes.put(attribute, new AttributesSnapshot.Entry(root.get("baseValue").getAsDouble(), Set.copyOf(modifiers)));
        });
        return new AttributesSnapshot(Map.copyOf(attributes));
    }

    @Override
    public Tag serialize(final AttributesSnapshot snapshot, final TagSerializationContext context) throws ParserException {
        final var tag = CompoundTag.builder();
        snapshot.attributes().forEach((attribute, entry) -> {
            final var modifiers = CompoundTag.builder();
            entry.modifiers().forEach(modifier -> modifiers.put(modifier.key().asString(), CompoundTag.builder()
                    .put("operation", context.serialize(modifier.getOperation()))
                    .put("amount", modifier.getAmount())
                    .put("slot", context.serialize(modifier.getSlotGroup()))
                    .build()));
            tag.put(attribute.key().asString(), CompoundTag.builder()
                    .put("baseValue", entry.baseValue())
                    .put("modifiers", modifiers.build())
                    .build());
        });
        return tag.build();
    }
}
