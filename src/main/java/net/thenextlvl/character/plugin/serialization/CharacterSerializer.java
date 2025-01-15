package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.serialization.TagSerializer;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.ListTag;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.PlayerCharacter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterSerializer implements TagSerializer<Character<?>> {
    @Override
    public CompoundTag serialize(Character<?> character, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("type", context.serialize(character.getType()));
        if (character.getDisplayName() != null)
            tag.add("displayName", context.serialize(character.getDisplayName()));
        if (character.getSpawnLocation() != null)
            tag.add("location", context.serialize(character.getSpawnLocation()));
        tag.add("collidable", character.isCollidable());
        tag.add("displayNameVisible", character.isDisplayNameVisible());
        tag.add("gravity", character.hasGravity());
        tag.add("invincible", character.isInvincible());
        tag.add("pose", character.getPose().name());
        tag.add("ticking", character.isTicking());
        tag.add("visibleByDefault", character.isVisibleByDefault());
        var actions = new CompoundTag();
        character.getActions().forEach((name, clickAction) -> actions.add(name, context.serialize(clickAction)));
        if (!actions.isEmpty()) tag.add("clickActions", actions);
        return character instanceof PlayerCharacter player ? serialize(tag, player, context) : tag;
    }

    private CompoundTag serialize(CompoundTag tag, PlayerCharacter character, TagSerializationContext context) {
        if (character.getGameProfile().getId() != null)
            tag.add("uuid", context.serialize(character.getGameProfile().getId()));
        var properties = new ListTag<>(CompoundTag.ID);
        character.getGameProfile().getProperties().forEach(property ->
                properties.add(context.serialize(property)));
        tag.add("listed", character.isListed());
        tag.add("properties", properties);
        tag.add("realPlayer", character.isRealPlayer());
        tag.add("skinParts", (byte) character.getSkinParts().getRaw());
        return tag;
    }
}
