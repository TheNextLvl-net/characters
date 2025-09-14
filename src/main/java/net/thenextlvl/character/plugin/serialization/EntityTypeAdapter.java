package net.thenextlvl.character.plugin.serialization;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public final class EntityTypeAdapter implements TagAdapter<EntityType> {
    @Override
    public EntityType deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var key = context.deserialize(tag, Key.class);
        return Arrays.stream(EntityType.values())
                .filter(type -> type.key().equals(key))
                .findAny()
                .orElseThrow(() -> new ParserException("Unknown entity type: " + key));
    }

    @Override
    public Tag serialize(EntityType type, TagSerializationContext context) throws ParserException {
        return context.serialize(type.key());
    }
}
