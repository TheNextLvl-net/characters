package net.thenextlvl.character.plugin.model;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class PaperEntityEquipment {
    public final @Nullable ItemStack itemInMainHand;
    public final @Nullable ItemStack itemInOffHand;
    public final @Nullable ItemStack helmet;
    public final @Nullable ItemStack chestplate;
    public final @Nullable ItemStack leggings;
    public final @Nullable ItemStack boots;
    public final float itemInMainHandDropChance;
    public final float itemInOffHandDropChance;
    public final float helmetDropChance;
    public final float chestplateDropChance;
    public final float leggingsDropChance;
    public final float bootsDropChance;

    public PaperEntityEquipment(
            @Nullable final ItemStack itemInMainHand, @Nullable final ItemStack itemInOffHand,
            @Nullable final ItemStack helmet, @Nullable final ItemStack chestplate, @Nullable final ItemStack leggings, @Nullable final ItemStack boots,
            final float itemInMainHandDropChance, final float itemInOffHandDropChance,
            final float helmetDropChance, final float chestplateDropChance, final float leggingsDropChance, final float bootsDropChance
    ) {
        this.itemInMainHand = itemInMainHand;
        this.itemInOffHand = itemInOffHand;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.itemInMainHandDropChance = itemInMainHandDropChance;
        this.itemInOffHandDropChance = itemInOffHandDropChance;
        this.helmetDropChance = helmetDropChance;
        this.chestplateDropChance = chestplateDropChance;
        this.leggingsDropChance = leggingsDropChance;
        this.bootsDropChance = bootsDropChance;
    }

    public PaperEntityEquipment(final EntityEquipment equipment) {
        this.itemInMainHand = equipment.getItemInMainHand();
        this.itemInOffHand = equipment.getItemInOffHand();
        this.helmet = equipment.getHelmet();
        this.chestplate = equipment.getChestplate();
        this.leggings = equipment.getLeggings();
        this.boots = equipment.getBoots();
        this.itemInMainHandDropChance = equipment.getItemInMainHandDropChance();
        this.itemInOffHandDropChance = equipment.getItemInOffHandDropChance();
        this.helmetDropChance = equipment.getHelmetDropChance();
        this.chestplateDropChance = equipment.getChestplateDropChance();
        this.leggingsDropChance = equipment.getLeggingsDropChance();
        this.bootsDropChance = equipment.getBootsDropChance();
    }

    public static @Nullable PaperEntityEquipment of(final LivingEntity entity) {
        final var equipment = entity.getEquipment();
        return equipment == null ? null : new PaperEntityEquipment(equipment);
    }

    public void apply(final LivingEntity entity) {
        final var equipment = entity.getEquipment();
        if (equipment != null) apply(equipment);
    }

    public void apply(final EntityEquipment equipment) {
        equipment.setItemInMainHand(itemInMainHand);
        equipment.setItemInOffHand(itemInOffHand);
        equipment.setHelmet(helmet);
        equipment.setChestplate(chestplate);
        equipment.setLeggings(leggings);
        equipment.setBoots(boots);
        if (!(equipment.getHolder() instanceof Mob)) return;
        equipment.setItemInMainHandDropChance(itemInMainHandDropChance);
        equipment.setItemInOffHandDropChance(itemInOffHandDropChance);
        equipment.setHelmetDropChance(helmetDropChance);
        equipment.setChestplateDropChance(chestplateDropChance);
        equipment.setLeggingsDropChance(leggingsDropChance);
        equipment.setBootsDropChance(bootsDropChance);
    }
}
