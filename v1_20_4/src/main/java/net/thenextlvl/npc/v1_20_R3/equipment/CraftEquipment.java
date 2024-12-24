package net.thenextlvl.npc.v1_20_R3.equipment;

import lombok.Getter;
import net.thenextlvl.character.equipment.Equipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CraftEquipment implements Equipment {
    private Map<EquipmentSlot, ItemStack> items = new HashMap<>();

    public @Nullable ItemStack getItem(EquipmentSlot slot) {
        return items.get(slot);
    }

    public void setItem(EquipmentSlot slot, ItemStack itemStack) {
        items.put(slot, itemStack);
    }

    @Override
    public void removeItem(EquipmentSlot slot) {
        items.remove(slot);
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public CraftEquipment clone() {
        try {
            var equipment = (CraftEquipment) super.clone();
            equipment.items = new HashMap<>(this.items);
            return equipment;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
