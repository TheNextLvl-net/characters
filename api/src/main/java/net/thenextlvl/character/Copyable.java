package net.thenextlvl.character;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents an object that can be copied from another object.
 *
 * @since 0.9.0
 */
@ApiStatus.NonExtendable
public interface Copyable<T> {
    /**
     * Copies the state of the given object into this object.
     *
     * @param other the object to copy from
     * @return this object with applied state of the given object
     * @since 0.9.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    T copyFrom(T other);
}
