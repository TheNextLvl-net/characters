package net.thenextlvl.character.plugin.model;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Random;

@NullMarked
public class EmptyLootTable implements LootTable {
    public static final LootTable INSTANCE = new EmptyLootTable();

    private EmptyLootTable() {
    }

    @Override
    public Collection<ItemStack> populateLoot(@Nullable Random random, LootContext context) {
        return List.of();
    }

    @Override
    public void fillInventory(Inventory inventory, @Nullable Random random, LootContext context) {
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey("characters", "loot_empty");
    }
}
