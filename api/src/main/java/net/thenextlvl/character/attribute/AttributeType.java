package net.thenextlvl.character.attribute;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class AttributeType<T> {
    private final @NonNull Class<T> dataType;
    private final @NonNull String name;

    public AttributeType(@NonNull String name, @NonNull Class<T> dataType) {
        this.dataType = dataType;
        this.name = name;
    }

    public @NonNull Class<T> getDataType() {
        return dataType;
    }

    public @NonNull String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AttributeType<?> that = (AttributeType<?>) o;
        return Objects.equals(dataType, that.dataType) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, name);
    }

    @Override
    public String toString() {
        return "AttributeType{" +
               "dataType=" + dataType +
               ", name='" + name + '\'' +
               '}';
    }
}
