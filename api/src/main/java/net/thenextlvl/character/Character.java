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
    Optional<ClickAction<?>> getAction(String name);

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

    @Contract(mutates = "this")
    <T> boolean addAction(String name, ClickAction<T> action);

    @Contract(mutates = "this")
    boolean addViewer(UUID player);

    @Contract(mutates = "this")
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

    @Contract(mutates = "io,this")
    boolean persist();

    @Contract(mutates = "this")
    boolean removeAction(String name);

    @Contract(mutates = "this")
    boolean removeViewer(UUID player);

    @Contract(mutates = "this")
    boolean removeViewers(Collection<UUID> players);

    boolean respawn();

    boolean respawn(Location location);

    @Contract(mutates = "this")
    boolean setDisplayName(@Nullable Component displayName);

    @Contract(mutates = "this")
    boolean setDisplayNameVisible(boolean visible);

    @Contract(mutates = "this")
    boolean setPathfinding(boolean pathfinding);

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

    boolean spawn();

    boolean spawn(Location location);

    // todo: return boolean and add delete event
    void delete();

    void remove();
}
