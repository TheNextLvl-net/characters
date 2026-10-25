package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EquipmentSlotGroupAdapter implements TagAdapter<EquipmentSlotGroup> {
    @Override
    public EquipmentSlotGroup deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var group = EquipmentSlotGroup.getByName(tag.getAsString());
        if (group != null) return group;
        throw new ParserException("Unknown equipment slot group: " + tag.getAsString());
    }

    @Override
    public Tag serialize(final EquipmentSlotGroup group, final TagSerializationContext context) throws ParserException {
        return StringTag.of(group.toString());
    }
}
