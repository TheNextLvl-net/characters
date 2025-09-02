package net.thenextlvl.character.codec;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NullMarked
final class SimpleEntityCodecRegistry implements EntityCodecRegistry {
    static final SimpleEntityCodecRegistry INSTANCE = new SimpleEntityCodecRegistry();

    private final Set<EntityCodec<?, ?>> codecs = new HashSet<>();

    @Override
    public void register(EntityCodec<?, ?> codec) throws IllegalStateException {
        Preconditions.checkState(codecs.add(codec), "Codec with the same key is already registered: %s", codec.key());
    }

    @Override
    public void registerAll(Collection<EntityCodec<?, ?>> codecs) throws IllegalStateException {
        codecs.forEach(this::register);
    }

    @Override
    public boolean unregister(EntityCodec<?, ?> codec) {
        return codecs.remove(codec);
    }

    @Override
    public boolean isRegistered(EntityCodec<?, ?> codec) {
        return codecs.contains(codec);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E, T> Optional<EntityCodec<E, T>> getCodec(Key key) {
        return codecs.stream().filter(codec -> codec.key().equals(key))
                .map(codec -> (EntityCodec<E, T>) codec)
                .findAny();
    }

    @Override
    public @Unmodifiable Set<EntityCodec<?, ?>> codecs() {
        return Set.copyOf(codecs);
    }
}
