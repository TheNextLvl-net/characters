package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.Tag;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RegistryAdapter<T extends Keyed> implements TagAdapter<T> {
    private final RegistryKey<T> registryKey;

    public RegistryAdapter(RegistryKey<T> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public T deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var registry = RegistryAccess.registryAccess().getRegistry(registryKey);
        return registry.getOrThrow(context.deserialize(tag, Key.class));
    }

    @Override
    public Tag serialize(T object, TagSerializationContext context) throws ParserException {
        return context.serialize(object.key());
    }
}
