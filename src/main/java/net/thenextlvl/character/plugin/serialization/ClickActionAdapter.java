package net.thenextlvl.character.plugin.serialization;

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
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.util.EnumSet;
import java.util.stream.Collectors;

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
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(ClickType.class)));
        var input = context.deserialize(root.get("input"), actionType.type());
        var chance = root.optional("chance").map(Tag::getAsInt).orElse(100);
        return new ClickAction<>(actionType, clickTypes, input, chance, cooldown, permission);
    }

    @Override
    public Tag serialize(ClickAction<?> action, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.empty();
        if (action.getPermission() != null) tag.add("permission", action.getPermission());
        if (!action.getCooldown().isZero()) tag.add("cooldown", action.getCooldown().toMillis());
        tag.add("actionType", context.serialize(action.getActionType()));
        var types = ListTag.<StringTag>of(StringTag.ID);
        for (var type : action.getClickTypes()) types.add(StringTag.of(type.name()));
        tag.add("clickTypes", types);
        tag.add("input", context.serialize(action.getInput()));
        tag.add("chance", action.getChance());
        return tag;
    }
}
