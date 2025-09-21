package net.thenextlvl.character;

import com.destroystokyo.paper.entity.Pathfinder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.tag.TagOptions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NullMarked
public interface Character<E extends Entity> {
    @Contract(pure = true)
    Optional<ClickAction<?>> getAction(String name);

    @Unmodifiable
    @Contract(pure = true)
    Map<String, ClickAction<?>> getActions();

    @Contract(pure = true)
    Optional<Component> getDisplayName();

    @Unmodifiable
    @Contract(pure = true)
    Set<Goal> getGoals();

    @Contract(mutates = "this")
    boolean addGoal(Goal goal);

    @Contract(mutates = "this")
    boolean removeGoal(Goal goal);

    @Contract(pure = true)
    <T> Optional<T> getEntity(Class<T> type);

    @Contract(pure = true)
    Optional<E> getEntity();

    @Contract(pure = true)
    Class<? extends E> getEntityClass();

    @Contract(pure = true)
    Optional<NamedTextColor> getTeamColor();

    @Contract(pure = true)
    Optional<Location> getLocation();

    @Contract(pure = true)
    String getName();

    @Contract(pure = true)
    Optional<String> getViewPermission();

    @Contract(pure = true)
    Optional<Location> getSpawnLocation();

    @Contract(pure = true)
    TagOptions getTagOptions();

    @Contract(pure = true)
    EntityType getType();

    @Unmodifiable
    @Contract(pure = true)
    Set<UUID> getViewers();

    @Contract(pure = true)
    Optional<World> getWorld();

    @Contract(pure = true)
    Optional<Pathfinder> getPathfinder();

    @Contract(mutates = "this")
    <T> boolean addAction(String name, ClickAction<T> action);

    @Contract(mutates = "this")
    boolean addViewer(UUID player);

    @Contract(mutates = "this")
    boolean addViewers(Collection<UUID> players);

    @Contract(pure = true)
    boolean canSee(Player player);

    @Contract(pure = true)
    boolean hasAction(ClickAction<?> action);

    @Contract(pure = true)
    boolean hasAction(String name);

    @Contract(pure = true)
    boolean isDisplayNameVisible();

    @Contract(pure = true)
    boolean isPersistent();

    @Contract(pure = true)
    boolean isSpawned();

    @Contract(pure = true)
    boolean isTrackedBy(Player player);

    @Contract(pure = true)
    boolean isViewer(UUID player);

    @Contract(pure = true)
    boolean isVisibleByDefault();

    @Contract(mutates = "io,this")
    boolean persist();

    @Contract(mutates = "this")
    boolean removeAction(String name);

    @Contract(mutates = "this")
    boolean removeViewer(UUID player);

    @Contract(mutates = "this")
    boolean removeViewers(Collection<UUID> players);

    @Contract(mutates = "this")
    boolean setDisplayName(@Nullable Component displayName);

    @Contract(mutates = "this")
    boolean setDisplayNameVisible(boolean visible);

    @Contract(mutates = "this")
    boolean setPersistent(boolean persistent);

    @Contract(mutates = "this")
    boolean setSpawnLocation(@Nullable Location location);

    @Contract(mutates = "this")
    boolean setTeamColor(@Nullable NamedTextColor color);

    @Contract(mutates = "this")
    boolean setViewPermission(@Nullable String permission);

    @Contract(mutates = "this")
    boolean setVisibleByDefault(boolean visible);

    @Nullable
    @Contract(mutates = "this")
    E spawn() throws IllegalStateException;

    @Contract(mutates = "this")
    E spawn(Location location) throws IllegalStateException;

    @Nullable
    @Contract(mutates = "this")
    E respawn() throws IllegalStateException;

    @Contract(mutates = "this")
    E respawn(Location location) throws IllegalStateException;

    // todo: return boolean and add delete event
    @Contract(mutates = "this")
    void delete();

    @Contract(mutates = "this")
    void remove();
}
