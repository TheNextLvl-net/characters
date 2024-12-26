package net.thenextlvl.character.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.StringTag;
import core.nbt.tag.Tag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ComponentAdapter implements TagAdapter<Component> {
    @Override
    public Component deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return MiniMessage.miniMessage().deserialize(tag.getAsString());
    }

    @Override
    public Tag serialize(Component component, TagSerializationContext context) throws ParserException {
        return new StringTag(MiniMessage.miniMessage().serialize(component));
    }
}
