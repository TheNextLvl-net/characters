package net.thenextlvl.character.property;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/**
 * Represents a property of a character's entity.
 * <p>
 * Property values are cached and persisted by the character
 * and applied to its entity whenever it spawns.
 *
 * @param <E> the entity type this property applies to
 * @param <T> the value type of this property
 * @see EntityProperties
 * @see net.thenextlvl.character.Character#setProperty(EntityProperty, Object)
 * @since 0.9.0
 */
public final class EntityProperty<E, T> implements Keyed {
    private final Key key;
    private final Class<E> entityType;
    private final Class<T> valueType;

    EntityProperty(final Key key, final Class<E> entityType, final Class<T> valueType) {
        this.key = key;
        this.entityType = entityType;
        this.valueType = valueType;
    }

    /**
     * Returns the key of this property.
     *
     * @return property key
     * @since 0.9.0
     */
    @Override
    @Contract(pure = true)
    public Key key() {
        return key;
    }

    /**
     * Returns the entity type this property applies to.
     *
     * @return entity type
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Class<E> entityType() {
        return entityType;
    }

    /**
     * Returns the value type of this property.
     *
     * @return value type
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Class<T> valueType() {
        return valueType;
    }

    /**
     * Checks if this property applies to entities of the given class.
     *
     * @param entityClass the entity class to check
     * @return {@code true} if this property applies to the given entity class, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    public boolean isApplicable(final Class<?> entityClass) {
        return entityType.isAssignableFrom(entityClass);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        return o instanceof final EntityProperty<?, ?> that && key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "EntityProperty{key=" + key.asString() + '}';
    }
}
