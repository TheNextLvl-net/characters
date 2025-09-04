package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.StringTag;
import core.nbt.tag.Tag;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public class EquipmentSlotGroupAdapter implements TagAdapter<EquipmentSlotGroup> {
    private static final Map<String, EquipmentSlotGroup> NAMES = Map.ofEntries(
            Map.entry("any", EquipmentSlotGroup.ANY),
            Map.entry("mainhand", EquipmentSlotGroup.MAINHAND),
            Map.entry("offhand", EquipmentSlotGroup.OFFHAND),
            Map.entry("hand", EquipmentSlotGroup.HAND),
            Map.entry("feet", EquipmentSlotGroup.FEET),
            Map.entry("legs", EquipmentSlotGroup.LEGS),
            Map.entry("chest", EquipmentSlotGroup.CHEST),
            Map.entry("head", EquipmentSlotGroup.HEAD),
            Map.entry("armor", EquipmentSlotGroup.ARMOR),
            Map.entry("body", EquipmentSlotGroup.BODY),
            Map.entry("saddle", EquipmentSlotGroup.SADDLE)
    );

    @Override
    public EquipmentSlotGroup deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return NAMES.get(tag.getAsString());
    }

    @Override
    public Tag serialize(EquipmentSlotGroup group, TagSerializationContext context) throws ParserException {
        return NAMES.entrySet().stream()
                .filter(entry -> entry.getValue() == group)
                .findAny()
                .map(entry -> new StringTag(entry.getKey()))
                .orElseThrow(() -> new ParserException("Unknown equipment slot group: " + group));
    }
}
