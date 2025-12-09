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
    public Set<AttributeInstance> deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var attributes = new HashSet<AttributeInstance>();
        var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE);
        root.forEach((key, tag1) -> {
            var attribute = registry.get(Key.key(key));
            if (attribute == null) return;
            var attributeTag = tag1.getAsCompound();
            var baseValue = attributeTag.get("baseValue").getAsDouble();
            var modifiers = new HashSet<AttributeModifier>();
            attributeTag.optional("modifiers").map(Tag::getAsCompound).ifPresent(modifiersTag -> {
                modifiersTag.forEach((key1, modifierTag) -> {
                    var asCompound = modifierTag.getAsCompound();
                    var modifierKey = NamespacedKey.fromString(key1);
                    if (modifierKey == null) return;
                    var amount = asCompound.get("amount").getAsInt();
                    var operation = context.deserialize(asCompound.get("operation"), AttributeModifier.Operation.class);
                    var slot = asCompound.optional("slot").map(tag2 -> context.deserialize(tag2, EquipmentSlotGroup.class)).orElse(EquipmentSlotGroup.ANY);
                    modifiers.add(new AttributeModifier(modifierKey, amount, operation, slot));
                });
            });
            attributes.add(new SimpleAttributeInstance(attribute, baseValue, modifiers));
        });
        return attributes;
    }

    @Override
    public Tag serialize(Set<AttributeInstance> attributes, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.empty();
        attributes.forEach(instance -> {
            var attributeTag = CompoundTag.empty();
            attributeTag.add("baseValue", instance.getBaseValue());

            var modifiersTag = CompoundTag.empty();
            instance.getModifiers().forEach(modifier -> {
                var modifierTag = CompoundTag.empty();
                modifierTag.add("operation", context.serialize(modifier.getOperation()));
                modifierTag.add("amount", modifier.getAmount());
                modifierTag.add("slot", context.serialize(modifier.getSlotGroup()));
                modifiersTag.add(modifier.key().asString(), modifierTag);
            });

            attributeTag.add("modifiers", modifiersTag);
            tag.add(instance.getAttribute().key().asString(), attributeTag);
        });
        return tag;
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
        public void setBaseValue(double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<AttributeModifier> getModifiers() {
            return List.copyOf(modifiers);
        }

        @Override
        public @Nullable AttributeModifier getModifier(Key key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeModifier(Key key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public @Nullable AttributeModifier getModifier(UUID uuid) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeModifier(UUID uuid) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addModifier(AttributeModifier modifier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addTransientModifier(AttributeModifier modifier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeModifier(AttributeModifier modifier) {
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
