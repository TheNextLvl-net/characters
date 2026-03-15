package net.thenextlvl.character.plugin.serialization;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@NullMarked
public final class AttributeAdapter implements TagAdapter<Set<AttributeInstance>> {
    @Override
    @SuppressWarnings("PatternValidation")
    public Set<AttributeInstance> deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var attributes = new HashSet<AttributeInstance>();
        final var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE);
        root.forEach((key, tag1) -> {
            final var attribute = registry.get(Key.key(key));
            if (attribute == null) return;
            final var attributeTag = tag1.getAsCompound();
            final var baseValue = attributeTag.get("baseValue").getAsDouble();
            final var modifiers = new HashSet<AttributeModifier>();
            attributeTag.optional("modifiers").map(Tag::getAsCompound).ifPresent(modifiersTag -> {
                modifiersTag.forEach((key1, modifierTag) -> {
                    final var asCompound = modifierTag.getAsCompound();
                    final var modifierKey = NamespacedKey.fromString(key1);
                    if (modifierKey == null) return;
                    final var amount = asCompound.get("amount").getAsInt();
                    final var operation = context.deserialize(asCompound.get("operation"), AttributeModifier.Operation.class);
                    final var slot = asCompound.optional("slot").map(tag2 -> context.deserialize(tag2, EquipmentSlotGroup.class)).orElse(EquipmentSlotGroup.ANY);
                    modifiers.add(new AttributeModifier(modifierKey, amount, operation, slot));
                });
            });
            attributes.add(new SimpleAttributeInstance(attribute, baseValue, modifiers));
        });
        return attributes;
    }

    @Override
    public Tag serialize(final Set<AttributeInstance> attributes, final TagSerializationContext context) throws ParserException {
        final var tag = CompoundTag.builder();
        attributes.forEach(instance -> {
            final var attributeTag = CompoundTag.builder();
            attributeTag.put("baseValue", instance.getBaseValue());

            final var modifiersTag = CompoundTag.builder();
            instance.getModifiers().forEach(modifier -> {
                modifiersTag.put(modifier.key().asString(), CompoundTag.builder()
                        .put("operation", context.serialize(modifier.getOperation()))
                        .put("amount", modifier.getAmount())
                        .put("slot", context.serialize(modifier.getSlotGroup()))
                        .build());
            });

            attributeTag.put("modifiers", modifiersTag.build());
            tag.put(instance.getAttribute().key().asString(), attributeTag.build());
        });
        return tag.build();
    }

    private record SimpleAttributeInstance(
            Attribute attribute, double baseValue, Collection<AttributeModifier> modifiers
    ) implements AttributeInstance {

        @Override
        public Attribute getAttribute() {
            return attribute;
        }

        @Override
        public double getBaseValue() {
            return baseValue;
        }

        @Override
        public void setBaseValue(final double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<AttributeModifier> getModifiers() {
            return List.copyOf(modifiers);
        }

        @Override
        public @Nullable AttributeModifier getModifier(final Key key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeModifier(final Key key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public @Nullable AttributeModifier getModifier(final UUID uuid) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeModifier(final UUID uuid) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addModifier(final AttributeModifier modifier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addTransientModifier(final AttributeModifier modifier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeModifier(final AttributeModifier modifier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDefaultValue() {
            throw new UnsupportedOperationException();
        }
    }
}
