package net.thenextlvl.character.plugin.serialization;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ComponentAdapter implements TagAdapter<Component> {
    @Override
    public Component deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        return MiniMessage.miniMessage().deserialize(tag.getAsString());
    }

    @Override
    public Tag serialize(final Component component, final TagSerializationContext context) throws ParserException {
        return StringTag.of(MiniMessage.miniMessage().serialize(component));
    }
}
