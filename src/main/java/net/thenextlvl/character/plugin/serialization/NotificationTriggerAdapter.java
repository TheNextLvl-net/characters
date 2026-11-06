package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.notification.NotificationTrigger;
import net.thenextlvl.character.notification.NotificationTriggerRegistry;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class NotificationTriggerAdapter implements TagAdapter<NotificationTrigger> {
    @Override
    public NotificationTrigger deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        return NotificationTriggerRegistry.registry().getByName(tag.getAsString()).orElseThrow(() ->
                new ParserException("Unknown notification trigger: " + tag.getAsString()));
    }

    @Override
    public Tag serialize(final NotificationTrigger trigger, final TagSerializationContext context) throws ParserException {
        return StringTag.of(trigger.getName());
    }
}
