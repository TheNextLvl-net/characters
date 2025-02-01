package net.thenextlvl.character;

import core.nbt.serialization.TagSerializable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.attribute.Attribute;
import net.thenextlvl.character.attribute.AttributeType;
import net.thenextlvl.character.tag.TagOptions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NullMarked
public interface Character<E extends Entity> extends TagSerializable {
    @Nullable
    ClickAction<?> getAction(String name);

    Equipment getEquipment();

    @Unmodifiable
    Map<String, ClickAction<?>> getActions();

    @Nullable
    Component getDisplayName();

    <T> Optional<T> getEntity(Class<T> type);

    Optional<E> getEntity();

    @Nullable
    NamedTextColor getTeamColor();

    @Nullable
    Location getLocation();

    String getName();

    String getScoreboardName();

    @Nullable
    String getViewPermission();

    @Nullable
    Location getSpawnLocation();

    TagOptions getTagOptions();

    EntityType getType();

    @Unmodifiable
    Set<UUID> getViewers();

    <T> Optional<T> getAttributeValue(AttributeType<?, T> type);

    <T> boolean setAttributeValue(AttributeType<?, T> type, T value);

    <V, T> Optional<Attribute<V, T>> getAttribute(AttributeType<V, T> type);

    @Nullable
    World getWorld();

    boolean addAction(String name, ClickAction<?> action);

    boolean addViewer(UUID player);

    boolean addViewers(Collection<UUID> players);

    boolean canSee(Player player);

    boolean despawn();

    boolean hasAI();

    boolean hasAction(ClickAction<?> action);

    boolean hasAction(String name);

    boolean isDisplayNameVisible();

    boolean isPathfinding();

    boolean isPersistent();

    boolean isSpawned();

    boolean isTicking();

    boolean isTrackedBy(Player player);

    boolean isViewer(UUID player);

    boolean isVisibleByDefault();

    boolean persist();

    boolean removeAction(String name);

    boolean removeViewer(UUID player);

    boolean removeViewers(Collection<UUID> players);

    boolean respawn();

    boolean respawn(Location location);

    boolean setAI(boolean ai);

    boolean setDisplayName(@Nullable Component displayName);

    boolean setDisplayNameVisible(boolean visible);

    boolean setPathfinding(boolean pathfinding);

    boolean setPersistent(boolean persistent);

    boolean setScale(double scale);

    boolean setSpawnLocation(@Nullable Location location);

    boolean setTeamColor(@Nullable NamedTextColor color);

    boolean setTicking(boolean ticking);

    boolean setViewPermission(@Nullable String permission);

    boolean setVisibleByDefault(boolean visible);

    boolean spawn();

    boolean spawn(Location location);

    double getScale();

    void delete();

    void remove();

    interface Equipment extends TagSerializable {
        @Unmodifiable
        EnumSet<EquipmentSlot> getSlots();

        @Nullable
        ItemStack getItem(EquipmentSlot slot);

        @Unmodifiable
        Map<EquipmentSlot, @Nullable ItemStack> getItems();

        boolean clear();

        boolean setItem(EquipmentSlot slot, @Nullable ItemStack item);

        boolean setItem(EquipmentSlot slot, @Nullable ItemStack item, boolean silent);
    }
}
