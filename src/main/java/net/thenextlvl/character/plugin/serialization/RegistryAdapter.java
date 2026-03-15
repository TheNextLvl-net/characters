package net.thenextlvl.character.plugin.serialization;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Keyed;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RegistryAdapter<T extends Keyed> implements TagAdapter<T> {
    private final RegistryKey<T> registryKey;

    public RegistryAdapter(final RegistryKey<T> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public T deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var registry = RegistryAccess.registryAccess().getRegistry(registryKey);
        return registry.getOrThrow(context.deserialize(tag, Key.class));
    }

    @Override
    public Tag serialize(final T object, final TagSerializationContext context) throws ParserException {
        return context.serialize(object.key());
    }
}
