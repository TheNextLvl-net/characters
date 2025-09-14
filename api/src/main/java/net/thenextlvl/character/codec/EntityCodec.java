package net.thenextlvl.character.codec;

import com.mojang.brigadier.arguments.ArgumentType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.thenextlvl.nbt.serialization.TagAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Represents a codec used for serializing and deserializing a specific value on a specific entity type.
 * The codec links an entity of type {@code E} and a value of type {@code T} through the use of a getter
 * and a setter. It also provides Brigadier argument type parsing and tag-based serialization support.
 *
 * @param <E> the type of entity
 * @param <T> the type of value associated with the entity
 * @since 0.4.0
 */
@ApiStatus.NonExtendable
public interface EntityCodec<E, T> extends Keyed {
    /**
     * Retrieves the class type representing the entity associated with this codec.
     *
     * @return the class type of the entity, represented as {@code Class<E>}
     */
    @NonNull
    @Contract(pure = true)
    Class<E> entityType();

    /**
     * Retrieves the class type representing the value associated with this codec.
     *
     * @return the class type of the value, represented as {@code Class<T>}
     */
    @NonNull
    @Contract(pure = true)
    Class<T> valueType();

    /**
     * Returns the getter used to read the current value from the entity.
     *
     * @return the getter function
     */
    @NonNull
    @Contract(pure = true)
    Function<E, T> getter();

    /**
     * Returns the setter used to write a value to the entity.
     *
     * @return the setter function
     */
    @NonNull
    @Contract(pure = true)
    BiPredicate<E, T> setter();

    /**
     * Brigadier argument type that parses command input into {@link T} so it can be fed into {@link #setter()}.
     *
     * @return the argument type used for parsing input values, or {@code null} if no argument type is defined
     */
    @Nullable
    @Contract(pure = true)
    ArgumentType<T> argumentType();

    /**
     * Retrieves the TagAdapter instance associated with this codec.
     *
     * @return the TagAdapter instance of type {@link T} used for serializing and deserializing values.
     */
    @NonNull
    @Contract(pure = true)
    TagAdapter<T> adapter();

    /**
     * Creates a new {@link Builder} instance for constructing {@link EntityCodec} objects.
     *
     * @param <E>        the type of the entity associated with the codec
     * @param <T>        the type of the value associated with the codec
     * @param key        the unique key identifying the codec
     * @param entityType the class type of the entity associated with the codec
     * @param valueType  the class type of the value associated with the codec
     * @return a new {@link Builder} instance for constructing configured {@link EntityCodec} objects
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static <E, T> @NonNull Builder<E, T> builder(@NonNull Key key, @NonNull Class<E> entityType, @NonNull Class<? super T> valueType) {
        return new SimpleEntityCodec.Builder<>(key, entityType, valueType);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <E> @NonNull Builder<E, String> stringCodec(@NonNull Key key, @NonNull Class<E> entityType) {
        return SimpleEntityCodec.stringCodec(key, entityType);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Duration> durationCodec(@NonNull Key key, @NonNull Class<E> entityType) {
        return durationCodec(key, entityType, Duration.ZERO);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Duration> durationCodec(@NonNull Key key, @NonNull Class<E> entityType, @NonNull Duration minimum) {
        return SimpleEntityCodec.durationCodec(key, entityType, minimum);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static <E, T extends Enum<T>> @NonNull Builder<E, T> enumCodec(@NonNull Key key, @NonNull Class<E> entityType, @NonNull Class<T> enumType) {
        return SimpleEntityCodec.enumCodec(key, entityType, enumType);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Boolean> booleanCodec(@NonNull Key key, @NonNull Class<E> entityType) {
        return SimpleEntityCodec.booleanCodec(key, entityType);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Integer> intCodec(@NonNull Key key, @NonNull Class<E> entityType) {
        return intCodec(key, entityType, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Integer> intCodec(@NonNull Key key, @NonNull Class<E> entityType, int min, int max) {
        return SimpleEntityCodec.intCodec(key, entityType, min, max);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Long> longCodec(@NonNull Key key, @NonNull Class<E> entityType) {
        return longCodec(key, entityType, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Long> longCodec(@NonNull Key key, @NonNull Class<E> entityType, long min, long max) {
        return SimpleEntityCodec.longCodec(key, entityType, min, max);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Double> doubleCodec(@NonNull Key key, @NonNull Class<E> entityType) {
        return doubleCodec(key, entityType, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Double> doubleCodec(@NonNull Key key, @NonNull Class<E> entityType, double min, double max) {
        return SimpleEntityCodec.doubleCodec(key, entityType, min, max);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Float> floatCodec(@NonNull Key key, @NonNull Class<E> entityType) {
        return floatCodec(key, entityType, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    static <E> @NonNull Builder<E, Float> floatCodec(@NonNull Key key, @NonNull Class<E> entityType, float min, float max) {
        return SimpleEntityCodec.floatCodec(key, entityType, min, max);
    }

    /**
     * Represents a builder interface for constructing {@link EntityCodec} instances.
     * This interface provides methods for configuring various aspects of an entity codec, such as
     * getter, setter, argument type, and adapter.
     *
     * @param <E> the type of the entity associated with the codec
     * @param <T> the type of the value associated with the codec
     */
    @ApiStatus.NonExtendable
    interface Builder<E, T> {
        /**
         * Sets the getter function responsible for retrieving the value from the entity.
         * The getter is a {@link Function} that takes an entity as input and returns a value of type {@code T}.
         *
         * @param getter a {@link Function} that retrieves the value for the entity
         * @return this builder instance, enabling method chaining
         */
        @NonNull
        @Contract(value = "_ -> this", mutates = "this")
        Builder<E, T> getter(@NonNull Function<E, @Nullable T> getter);

        /**
         * Sets the setter function responsible for updating the value of the entity.
         * The setter is a {@link BiPredicate} that takes an entity and a value as arguments.
         *
         * @param setter a {@link BiPredicate} that updates the value for the entity
         * @return this builder instance, enabling method chaining
         */
        @NonNull
        @Contract(value = "_ -> this", mutates = "this")
        Builder<E, T> setter(@NonNull BiPredicate<E, T> setter);

        /**
         * Sets the setter function responsible for updating the value of the entity.
         * The setter is a {@link BiConsumer} that takes an entity and a value as arguments.
         * <p>
         * It evaluates whether the value actually changed on the entity.
         *
         * @param setter a {@link BiConsumer} that updates the value on the entity
         * @return this builder instance, enabling method chaining
         * @throws IllegalArgumentException if the provided setter is invalid
         * @apiNote A getter must be defined before using this setter.
         */
        @NonNull
        @Contract(value = "_ -> this", mutates = "this")
        Builder<E, T> setter(@NonNull BiConsumer<E, T> setter) throws IllegalArgumentException;

        /**
         * Sets the argument type to be used for parsing and handling input values of type {@code T}.
         *
         * @param argumentType the {@link ArgumentType} responsible for managing and validating
         *                     input values corresponding to type {@link T}
         * @return this builder instance, enabling method chaining
         */
        @NonNull
        @Contract(value = "_ -> this", mutates = "this")
        Builder<E, T> argumentType(@NonNull ArgumentType<T> argumentType);

        /**
         * Adds a {@link TagAdapter} to the builder, which will be used to handle the
         * serialization and deserialization of the specified type {@link T}.
         *
         * @param adapter the {@link TagAdapter} responsible for managing the transformation
         *                between the type {@link T} and its serialized form
         * @return this builder instance, enabling method chaining
         */
        @NonNull
        @Contract(value = "_ -> this", mutates = "this")
        Builder<E, T> adapter(@NonNull TagAdapter<T> adapter);

        /**
         * Constructs a new {@link EntityCodec} instance based on the configurations
         * set on the builder. This method finalizes the builder's state and produces a codec
         * for handling serialization, deserialization, and value management for an entity.
         *
         * @return a new instance of {@link EntityCodec} configured with the settings provided to the builder
         * @throws IllegalArgumentException if a required setting has not been provided
         */
        @NonNull
        @Contract(value = "-> new", pure = true)
        EntityCodec<E, T> build() throws IllegalArgumentException;
    }
}

