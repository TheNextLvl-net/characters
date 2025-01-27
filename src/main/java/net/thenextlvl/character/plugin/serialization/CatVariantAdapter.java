package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.Tag;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Cat;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CatVariantAdapter implements TagAdapter<Cat.Type> {
    @Override
    public Cat.Type deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.CAT_VARIANT)
                .getOrThrow(context.deserialize(tag, Key.class));
    }

    @Override
    public Tag serialize(Cat.Type type, TagSerializationContext context) throws ParserException {
        return context.serialize(type.key());
    }
}
