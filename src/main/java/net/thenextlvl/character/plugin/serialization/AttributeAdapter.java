package net.thenextlvl.character.plugin.serialization;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class AttributeAdapter implements TagAdapter<Set<AttributeInstance>> {
    @Override
    public Set<AttributeInstance> deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        // todo: implement
        return Set.of();
    }

    @Override
    public Tag serialize(Set<AttributeInstance> attributes, TagSerializationContext context) throws ParserException {
        var tag = ListTag.<CompoundTag>of(CompoundTag.ID);
        attributes.forEach(instance -> {
            var attributeTag = CompoundTag.empty();
            attributeTag.add("attribute", context.serialize(instance.getAttribute().key()));
            attributeTag.add("baseValue", instance.getBaseValue());
            
            var modifiersTag = CompoundTag.empty();
            instance.getModifiers().forEach(modifier -> {
                modifiersTag.add("key", context.serialize(modifier.key()));
                modifiersTag.add("operation", context.serialize(modifier.key()));
                modifiersTag.add("amount", modifier.getAmount());
                modifiersTag.add("slot", context.serialize(modifier.getSlotGroup()));
            });
            
            attributeTag.add("modifiers", modifiersTag);
            tag.add(attributeTag);
        });
        return tag;
    }

    private static final class SimpleAttributeInstance implements AttributeInstance {
        private final Attribute attribute;
        private final double baseValue;
        private final Collection<AttributeModifier> modifiers;
        
        public SimpleAttributeInstance(Attribute attribute, double baseValue, Collection<AttributeModifier> modifiers) {
            this.attribute = attribute;
            this.baseValue = baseValue;
            this.modifiers = modifiers;
        }

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
