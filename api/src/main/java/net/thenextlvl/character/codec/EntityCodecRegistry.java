package net.thenextlvl.character.codec;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A registry for managing {@link EntityCodec} instances, allowing registration,
 * unregistration, and querying of codecs based on their associated keys.
 *
 * @since 0.4.0
 */
@NullMarked
@ApiStatus.NonExtendable
public interface EntityCodecRegistry {
    /**
     * Provides the singleton instance of {@link EntityCodecRegistry}.
     *
     * @return the singleton instance of {@link EntityCodecRegistry}.
     */
    @Contract(pure = true)
    static EntityCodecRegistry registry() {
        return SimpleEntityCodecRegistry.INSTANCE;
    }

    /**
     * Registers the given {@link EntityCodec} instance within the registry.
     *
     * @param codec the codec to register, which associates keys with specific
     *              serialization and deserialization logic.
     * @throws IllegalStateException if a codec with the same key is already registered.
     */
    @Contract(mutates = "this")
    void register(EntityCodec<?, ?> codec) throws IllegalStateException;

    /**
     * Registers all {@link EntityCodec} instances provided in the given collection.
     * This method allows batch registration of codecs in the registry.
     *
     * @param codecs a collection of {@link EntityCodec} instances to register.
     * @throws IllegalStateException if a codec with the same key is already registered.
     */
    @Contract(mutates = "this")
    void registerAll(Collection<EntityCodec<?, ?>> codecs) throws IllegalStateException;

    /**
     * Unregisters the specified {@link EntityCodec} from the registry.
     *
     * @param codec the codec to be unregistered.
     * @return {@code true} if the codec was successfully unregistered, {@code false} otherwise.
     */
    @Contract(mutates = "this")
    boolean unregister(EntityCodec<?, ?> codec);

    /**
     * Checks whether the specified {@link EntityCodec} is currently registered in the registry.
     *
     * @param codec the codec to check for registration within the registry.
     * @return {@code true} if the codec is registered, {@code false} otherwise.
     */
    @Contract(pure = true)
    boolean isRegistered(EntityCodec<?, ?> codec);

    /**
     * Retrieves an {@link EntityCodec} associated with the specified {@link Key}, if present.
     *
     * @param key the key representing the codec to be retrieved.
     * @return an {@link Optional} containing the associated {@link EntityCodec},
     * or an empty {@link Optional} if no codec is found for the given key.
     */
    @Contract(pure = true)
    <E, T> Optional<EntityCodec<E, T>> getCodec(Key key);

    /**
     * Retrieves an unmodifiable set of all registered {@link EntityCodec} instances.
     *
     * @return an unmodifiable {@link Set} containing all the registered {@link EntityCodec} instances.
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<EntityCodec<?, ?>> codecs();
}
