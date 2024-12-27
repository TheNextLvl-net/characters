package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.serialization.TagSerializer;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.ListTag;
import core.nbt.tag.Tag;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.PlayerCharacter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterSerializer implements TagSerializer<Character<?>> {
    @Override
    public Tag serialize(Character<?> character, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("type", context.serialize(character.getType()));
        if (character.getDisplayName() != null)
            tag.add("displayName", context.serialize(character.getDisplayName()));
        if (character.getSpawnLocation() != null)
            tag.add("location", context.serialize(character.getSpawnLocation()));
        tag.add("collidable", character.isCollidable());
        tag.add("invincible", character.isInvincible());
        tag.add("visibleByDefault", character.isVisibleByDefault());
        if (character instanceof PlayerCharacter playerCharacter) {
            if (playerCharacter.getGameProfile().getId() != null)
                tag.add("uuid", context.serialize(playerCharacter.getGameProfile().getId()));
            var properties = new ListTag<>(CompoundTag.ID);
            playerCharacter.getGameProfile().getProperties().forEach(property ->
                    properties.add(context.serialize(property)));
            tag.add("listed", playerCharacter.isListed());
            tag.add("properties", properties);
            tag.add("realPlayer", playerCharacter.isRealPlayer());
            tag.add("skinParts", (byte) playerCharacter.getSkinParts().getRaw());
        }
        return tag;
    }
}
