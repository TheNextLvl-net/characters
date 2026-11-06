package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.notification.NotificationType;
import net.thenextlvl.character.notification.NotificationTypeRegistry;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class NotificationTypeAdapter implements TagAdapter<NotificationType<?>> {
    @Override
    public NotificationType<?> deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        return NotificationTypeRegistry.registry().getByName(tag.getAsString()).orElseThrow(() ->
                new ParserException("Unknown notification type: " + tag.getAsString()));
    }

    @Override
    public Tag serialize(final NotificationType<?> type, final TagSerializationContext context) throws ParserException {
        return StringTag.of(type.getName());
    }
}
