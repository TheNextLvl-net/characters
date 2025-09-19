package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ActionTypeRegistry;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ActionTypeAdapter implements TagAdapter<ActionType<?>> {
    @Override
    public ActionType<?> deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return ActionTypeRegistry.registry().getByName(tag.getAsString()).orElseThrow();
    }

    @Override
    public Tag serialize(ActionType<?> type, TagSerializationContext context) throws ParserException {
        return StringTag.of(type.name());
    }
}
