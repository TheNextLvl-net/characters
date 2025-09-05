package net.thenextlvl.character.plugin.serialization;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.Tag;
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
