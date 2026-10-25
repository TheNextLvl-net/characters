package net.thenextlvl.character.plugin.property;

import com.mojang.brigadier.arguments.ArgumentType;
import net.kyori.adventure.key.Key;
import net.thenextlvl.character.property.EntityProperty;
import net.thenextlvl.nbt.serialization.NBT;
import net.thenextlvl.nbt.tag.ByteTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Reads and writes an {@link EntityProperty} on an entity, optionally settable via command.
 * <p>
 * Values are (de)serialized through the plugin's {@link NBT} instance using {@link EntityProperty#valueType()},
 * so every value type must have an adapter registered there.
 */
@NullMarked
public record PropertyAccessor<E, T>(
        EntityProperty<E, T> property,
        Function<E, @Nullable T> getter,
        BiConsumer<E, @Nullable T> setter,
        @Nullable ArgumentType<T> argumentType
) {
    private static final ByteTag NULL = ByteTag.of((byte) -1);

    public Key key() {
        return property.key();
    }

    public @Nullable T get(final Object entity) {
        final var entityType = property.entityType();
        return entityType.isInstance(entity) ? getter.apply(entityType.cast(entity)) : null;
    }

    public Tag serialize(@Nullable final T value, final NBT nbt) {
        return value == null ? NULL : nbt.serialize(value, property.valueType());
    }

    public @Nullable T deserialize(final Tag tag, final NBT nbt) {
        return isNull(tag) ? null : nbt.deserialize(tag, property.valueType());
    }

    public void apply(final Object entity, final Tag tag, final NBT nbt) {
        final var entityType = property.entityType();
        if (!entityType.isInstance(entity)) return;
        setter.accept(entityType.cast(entity), deserialize(tag, nbt));
    }

    private static boolean isNull(final Tag tag) {
        return tag instanceof final ByteTag byteTag && byteTag.getAsByte() == NULL.getAsByte();
    }
}
