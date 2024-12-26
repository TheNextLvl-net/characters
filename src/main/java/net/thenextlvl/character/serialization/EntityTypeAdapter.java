package net.thenextlvl.character.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.Tag;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public class EntityTypeAdapter implements TagAdapter<EntityType> {
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
