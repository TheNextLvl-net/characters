package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.StringTag;
import core.nbt.tag.Tag;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ActionTypeAdapter implements TagAdapter<ActionType<?>> {
    private final CharacterPlugin plugin;

    public ActionTypeAdapter(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ActionType<?> deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return plugin.characterProvider().getActionRegistry().getByName(tag.getAsString()).orElseThrow();
    }

    @Override
    public Tag serialize(ActionType<?> type, TagSerializationContext context) throws ParserException {
        return new StringTag(type.name());
    }
}
