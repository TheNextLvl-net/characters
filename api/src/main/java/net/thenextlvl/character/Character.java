package net.thenextlvl.character;

import com.destroystokyo.paper.entity.Pathfinder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.tag.TagOptions;
import net.thenextlvl.nbt.serialization.TagSerializable;
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
public interface Character<E extends Entity> {
    Optional<ClickAction<?>> getAction(String name);

    Equipment getEquipment();

    @Unmodifiable
    Map<String, ClickAction<?>> getActions();

    Optional<Component> getDisplayName();

    @Unmodifiable
    Set<Goal> getGoals();

    boolean addGoal(Goal goal);

    boolean removeGoal(Goal goal);

    <T> Optional<T> getEntity(Class<T> type);

    Optional<E> getEntity();

    Class<? extends E> getEntityClass();

    Optional<NamedTextColor> getTeamColor();

    Optional<Location> getLocation();

    String getName();

    String getScoreboardName();

    Optional<String> getViewPermission();

    Optional<Location> getSpawnLocation();

    TagOptions getTagOptions();

    EntityType getType();

    @Unmodifiable
    Set<UUID> getViewers();

    Optional<World> getWorld();

    Optional<Pathfinder> getPathfinder();

    <T> boolean addAction(String name, ClickAction<T> action);

    boolean addViewer(UUID player);

    boolean addViewers(Collection<UUID> players);

    boolean canSee(Player player);

    boolean despawn();

    boolean hasAction(ClickAction<?> action);

    boolean hasAction(String name);

    boolean isDisplayNameVisible();

    boolean isPathfinding();

    boolean isPersistent();

    boolean isSpawned();

    boolean isTrackedBy(Player player);

    boolean isViewer(UUID player);

    boolean isVisibleByDefault();

    boolean persist();

    boolean removeAction(String name);

    boolean removeViewer(UUID player);

    boolean removeViewers(Collection<UUID> players);

    boolean respawn();

    boolean respawn(Location location);

    boolean setDisplayName(@Nullable Component displayName);

    boolean setDisplayNameVisible(boolean visible);

    boolean setPathfinding(boolean pathfinding);

    boolean setPersistent(boolean persistent);

    boolean setSpawnLocation(@Nullable Location location);

    boolean setTeamColor(@Nullable NamedTextColor color);

    boolean setViewPermission(@Nullable String permission);

    boolean setVisibleByDefault(boolean visible);

    boolean spawn();

    boolean spawn(Location location);

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
