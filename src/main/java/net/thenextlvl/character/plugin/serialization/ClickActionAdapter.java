package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.ListTag;
import core.nbt.tag.StringTag;
import core.nbt.tag.Tag;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.action.ClickType;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
public class ClickActionAdapter implements TagAdapter<ClickAction<?>> {
    @Override
    @SuppressWarnings("unchecked")
    public ClickAction<?> deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var permission = root.optional("permission").map(Tag::getAsString).orElse(null);
        var cooldown = root.optional("cooldown").map(Tag::getAsLong).map(Duration::ofMillis).orElse(Duration.ZERO);
        var actionType = (ActionType<Object>) context.deserialize(root.get("actionType"), ActionType.class);
        var clickTypes = root.<StringTag>getAsList("clickTypes").stream()
                .map(StringTag::getAsString)
                .map(ClickType::valueOf)
                .toArray(ClickType[]::new);
        var input = context.deserialize(root.get("input"), actionType.type());
        return new ClickAction<>(actionType, clickTypes, input, cooldown, permission);
    }

    @Override
    public Tag serialize(ClickAction<?> action, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        if (action.getPermission() != null) tag.add("permission", action.getPermission());
        if (!action.getCooldown().equals(Duration.ZERO)) tag.add("cooldown", action.getCooldown().toMillis());
        tag.add("actionType", context.serialize(action.getActionType()));
        var types = new ListTag<StringTag>(StringTag.ID);
        for (var type : action.getClickTypes()) types.add(new StringTag(type.name()));
        tag.add("clickTypes", types);
        tag.add("input", context.serialize(action.getInput()));
        return tag;
    }
}
