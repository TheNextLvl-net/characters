package net.thenextlvl.character.attribute;

import core.nbt.serialization.TagSerializable;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NonNull;

public interface Attribute<E extends Entity, T> extends TagSerializable {
    @NonNull
    AttributeType<@NonNull E, T> getType();

    T getValue();

    boolean setValue(T value);
}
