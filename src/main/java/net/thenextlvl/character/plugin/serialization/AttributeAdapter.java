package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.Tag;
import net.kyori.adventure.key.Key;
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
    public Tag serialize(Set<AttributeInstance> object, TagSerializationContext context) throws ParserException {
        // todo: implement
        return null;
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
