package net.thenextlvl.character.attribute;

import net.thenextlvl.nbt.serialization.TagSerializable;
import org.jspecify.annotations.NonNull;

// todo: revise, still needed?
public interface AttributeInstance<T> extends TagSerializable {
    @NonNull
    AttributeType<?, T> getType();

    @NonNull
    Class<T> getDataType();

    T getValue();

    boolean setValue(T value);
}
