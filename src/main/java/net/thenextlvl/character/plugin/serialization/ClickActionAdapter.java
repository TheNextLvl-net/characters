package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.notification.NotificationTrigger;
import net.thenextlvl.character.notification.NotificationType;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.action.ClickType;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.util.EnumSet;
import java.util.stream.Collectors;

@NullMarked
public final class ClickActionAdapter implements TagAdapter<ClickAction<?>> {
    @Override
    @SuppressWarnings("unchecked")
    public ClickAction<?> deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var permission = root.optional("permission").map(Tag::getAsString).orElse(null);
        final var cooldown = root.optional("cooldown").map(Tag::getAsLong).map(Duration::ofMillis).orElse(Duration.ZERO);
        final var actionType = (ActionType<@NonNull Object>) context.deserialize(root.get("actionType"), ActionType.class);
        final var clickTypes = root.<StringTag>getAsList("clickTypes").stream()
                .map(StringTag::getAsString)
                .map(ClickType::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(ClickType.class)));
        final var input = context.deserialize(root.get("input"), actionType.type());
        final var chance = root.optional("chance").map(Tag::getAsInt).orElse(100);
        final var action = ClickAction.factory().create(actionType, clickTypes, input);
        action.setChance(chance);
        action.setCooldown(cooldown);
        action.setPermission(permission);
        root.optional("notifications").map(Tag::getAsList).ifPresent(notifications -> notifications.forEach(notification ->
                deserializeNotification(action, notification.getAsCompound(), context)));
        return action;
    }

    @SuppressWarnings("unchecked")
    private static void deserializeNotification(final ClickAction<?> action, final CompoundTag tag, final TagDeserializationContext context) {
        final var trigger = context.deserialize(tag.get("trigger"), NotificationTrigger.class);
        final var type = (NotificationType<Object>) context.deserialize(tag.get("type"), NotificationType.class);
        action.getNotifications().set(trigger, type, context.deserialize(tag.get("input"), type.getType()));
    }

    @Override
    public Tag serialize(final ClickAction<?> action, final TagSerializationContext context) throws ParserException {
        final var builder = CompoundTag.builder();
        action.getPermission().ifPresent(permission -> builder.put("permission", permission));
        if (!action.getCooldown().isZero()) builder.put("cooldown", action.getCooldown().toMillis());
        if (!action.getNotifications().isEmpty()) builder.put("notifications", ListTag.of(action.getNotifications().stream()
                .map(notification -> (Tag) CompoundTag.builder()
                        .put("trigger", context.serialize(notification.trigger(), NotificationTrigger.class))
                        .put("type", context.serialize(notification.type(), NotificationType.class))
                        .put("input", context.serialize(notification.input(), notification.type().getType()))
                        .build())
                .toList()));
        builder.put("actionType", context.serialize(action.getActionType()));
        final var types = ListTag.<StringTag>builder().contentType(StringTag.ID);
        for (final var type : action.getClickTypes()) types.add(StringTag.of(type.name()));
        builder.put("clickTypes", types.build());
        builder.put("input", context.serialize(action.getInput()));
        builder.put("chance", action.getChance());
        return builder.build();
    }
}
