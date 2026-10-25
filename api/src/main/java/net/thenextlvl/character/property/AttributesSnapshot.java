package net.thenextlvl.character.property;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a snapshot of an entity's attributes.
 *
 * @param attributes the attribute entries per attribute
 * @see EntityProperties#ATTRIBUTABLE_ATTRIBUTES
 * @since 0.9.0
 */
public record AttributesSnapshot(@Unmodifiable Map<Attribute, Entry> attributes) {
    public AttributesSnapshot {
        attributes = Map.copyOf(attributes);
    }

    /**
     * Returns the entry of the given attribute.
     *
     * @param attribute the attribute
     * @return the entry of the given attribute
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Optional<Entry> getEntry(final Attribute attribute) {
        return Optional.ofNullable(attributes.get(attribute));
    }

    /**
     * Represents the state of a single attribute.
     *
     * @param baseValue the base value of the attribute
     * @param modifiers the modifiers of the attribute
     * @since 0.9.0
     */
    public record Entry(double baseValue, @Unmodifiable Set<AttributeModifier> modifiers) {
        public Entry {
            modifiers = Set.copyOf(modifiers);
        }
    }
}
