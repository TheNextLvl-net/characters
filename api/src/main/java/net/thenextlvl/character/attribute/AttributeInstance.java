package net.thenextlvl.character.attribute;

import core.nbt.serialization.TagSerializable;
import org.jspecify.annotations.NonNull;

public interface AttributeInstance<T> extends TagSerializable {
    @NonNull
    AttributeType<?, T> getType();

    @NonNull
    Class<T> getDataType();

    T getValue();

    void setValue(T value);
}
