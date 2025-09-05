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
