package net.thenextlvl.character.attribute;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.thenextlvl.character.Character;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

// todo: revise, still needed?
public class AttributeType<E, T> implements Keyed {
    private final @NonNull BiConsumer<@NonNull E, T> setter;
    private final @NonNull Class<E> entityType;
    private final @NonNull Class<T> dataType;
    private final @NonNull Function<@NonNull E, T> getter;
    private final @NonNull Function<@NonNull E, T> defaultGetter;
    private final @NonNull Key key;

    public AttributeType(@NonNull Key key, @NonNull Class<E> entityType, @NonNull Class<T> dataType,
                         @NonNull Function<@NonNull E, T> getter,
                         @NonNull Function<@NonNull E, T> defaultGetter,
                         @NonNull BiConsumer<@NonNull E, T> setter) {
        this.dataType = dataType;
        this.entityType = entityType;
        this.getter = getter;
        this.defaultGetter = defaultGetter;
        this.key = key;
        this.setter = setter;
    }

    public void set(@NonNull E entity, T value) {
        setter.accept(entity, value == null ? getDefault(entity) : value);
    }

    public T get(E entity) {
        return getter.apply(entity);
    }

    public T getOrDefault(E entity) {
        return Objects.requireNonNullElseGet(get(entity), () -> getDefault(entity));
    }

    public T getDefault(E entity) {
        return defaultGetter.apply(entity);
    }

    public @NonNull Class<T> dataType() {
        return dataType;
    }

    public @NonNull Class<E> entityType() {
        return entityType;
    }

    @Override
    public @NonNull Key key() {
        return key;
    }

    public boolean isApplicable(@NonNull Character<?> character) {
        var entityClass = character.getType().getEntityClass();
        return entityClass != null && entityType.isAssignableFrom(entityClass);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AttributeType<?, ?> that = (AttributeType<?, ?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "AttributeType{" +
               "entityType=" + entityType +
               ", dataType=" + dataType +
               ", key=" + key +
               '}';
    }
}
