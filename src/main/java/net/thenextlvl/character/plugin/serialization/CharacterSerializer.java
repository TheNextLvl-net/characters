package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.codec.EntityCodec;
import net.thenextlvl.character.codec.EntityCodecRegistry;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.serialization.TagSerializer;
import net.thenextlvl.nbt.tag.ByteTag;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterSerializer implements TagSerializer<Character<?>> {
    @Override
    public Tag serialize(Character<?> character, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.empty();

        if (character instanceof PlayerCharacter player) {
            var id = player.getGameProfile().getId();
            if (id != null) tag.add("uuid", context.serialize(id));
            var properties = ListTag.of(CompoundTag.ID);
            player.getGameProfile().getProperties().forEach(property -> properties.add(context.serialize(property)));
            tag.add("listed", player.isListed());
            tag.add("properties", properties);
            tag.add("realPlayer", player.isRealPlayer());
            tag.add("skinParts", (byte) player.getSkinParts().getRaw());
        }

        character.getDisplayName().ifPresent(displayName -> tag.add("displayName", context.serialize(displayName)));
        character.getSpawnLocation().ifPresent(spawnLocation -> tag.add("location", context.serialize(spawnLocation)));
        character.getTeamColor().ifPresent(teamColor -> tag.add("teamColor", context.serialize(teamColor)));
        character.getViewPermission().ifPresent(viewPermission -> tag.add("viewPermission", viewPermission));
        tag.add("displayNameVisible", character.isDisplayNameVisible());
        tag.add("pathfinding", character.isPathfinding());
        tag.add("tagOptions", character.getTagOptions().serialize());
        tag.add("type", context.serialize(character.getType()));
        tag.add("visibleByDefault", character.isVisibleByDefault());
        var actions = CompoundTag.empty();
        var attributes = CompoundTag.empty();
        character.getActions().forEach((name, clickAction) -> actions.add(name, context.serialize(clickAction)));
        character.getEntity().ifPresent(entity -> {
            var entityData = CompoundTag.empty();
            EntityCodecRegistry.registry().codecs().forEach(entityCodec -> {
                if (!entityCodec.entityType().isInstance(entity)) return;
                @SuppressWarnings("unchecked") var codec = (EntityCodec<Object, Object>) entityCodec;
                var object = codec.getter().apply(entity);
                if (object == null) entityData.add(codec.key().asString(), ByteTag.of((byte) -1));
                else entityData.add(codec.key().asString(), codec.adapter().serialize(object, context));
            });
            tag.add("entityData", entityData);
        });
        if (!actions.isEmpty()) tag.add("clickActions", actions);
        if (!attributes.isEmpty()) tag.add("attributes", attributes);
        return tag;
    }
}
