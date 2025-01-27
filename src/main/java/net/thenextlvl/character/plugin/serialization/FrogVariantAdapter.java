package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.Tag;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Frog;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FrogVariantAdapter implements TagAdapter<Frog.Variant> {
    @Override
    public Frog.Variant deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.FROG_VARIANT)
                .getOrThrow(context.deserialize(tag, Key.class));
    }

    @Override
    public Tag serialize(Frog.Variant variant, TagSerializationContext context) throws ParserException {
        return context.serialize(variant.key());
    }
}
