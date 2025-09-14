package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public final class EquipmentSlotGroupAdapter implements TagAdapter<EquipmentSlotGroup> {
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
                .map(entry -> StringTag.of(entry.getKey()))
                .orElseThrow(() -> new ParserException("Unknown equipment slot group: " + group));
    }
}
