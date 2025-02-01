package net.thenextlvl.character.attribute;

import core.nbt.serialization.TagSerializable;
import org.jspecify.annotations.NonNull;

public interface Attribute<E, T> extends TagSerializable {
    @NonNull
    AttributeType<E, T> getType();

    T getValue();

    boolean setValue(T value);
}
