package net.thenextlvl.character;

import com.destroystokyo.paper.entity.Pathfinder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.property.EntityProperties;
import net.thenextlvl.character.property.EntityProperty;
import net.thenextlvl.character.tag.TagOptions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * An interface that represents a character
 *
 * @since 0.9.0
 */
@ApiStatus.NonExtendable
public interface Character {
    /**
     * Returns the name of the character.
     *
     * @return character name
     * @since 0.1.0
     */
    @Contract(pure = true)
    String getName();

    /**
     * Returns the entity type of the character.
     *
     * @return entity type
     * @since 0.1.0
     */
    @Contract(pure = true)
    EntityType getType();

    /**
     * Returns the entity class of the character.
     *
     * @return entity class
     * @since 0.9.0
     */
    @Contract(pure = true)
    Class<? extends Entity> getEntityClass();

    /**
     * Returns the entity of the character.
     * <p>
     * The entity only exists while the character is spawned, which requires its chunk to be loaded.
     * Changes made directly to the entity are not persisted.
     *
     * @return the entity, or empty if the character is not spawned
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Entity> getEntity();

    /**
     * Returns the entity of the character if it is of the given type.
     *
     * @param type entity type
     * @param <T>  entity type
     * @return the entity, or empty if the character is not spawned or not of the given type
     * @since 0.1.0
     */
    @Contract(pure = true)
    <T> Optional<T> getEntity(Class<T> type);

    /**
     * Returns the pathfinder of the character's entity.
     *
     * @return the pathfinder, or empty if the character is not spawned or not a mob
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<Pathfinder> getPathfinder();

    /**
     * Returns the location of the character.
     * <p>
     * This is the location the character is spawned at,
     * regardless of whether its entity currently exists.
     *
     * @return the location of the character
     * @since 0.9.0
     */
    @Contract(value = " -> new", pure = true)
    Location getLocation();

    /**
     * Returns the world of the character.
     *
     * @return the world of the character
     * @since 0.9.0
     */
    @Contract(pure = true)
    World getWorld();

    /**
     * Teleports the character to the given location.
     * <p>
     * The new location is applied immediately, the entity is moved once it exists.
     * Calling this method may involve moving the character's data files.
     *
     * @param location new character location
     * @return {@link CompletableFuture} that completes with {@code true}
     * if the character was teleported, {@code false} otherwise
     * @throws IllegalArgumentException if the world of the location is {@code null}
     * @since 0.9.0
     */
    @Contract(mutates = "this,io")
    CompletableFuture<Boolean> teleportAsync(Location location) throws IllegalArgumentException;

    /**
     * Returns the display name of the character.
     *
     * @return display name
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<Component> getDisplayName();

    /**
     * Sets the display name of the character.
     *
     * @param displayName new display name
     * @return {@code true} if the display name was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setDisplayName(@Nullable Component displayName);

    /**
     * Checks if the display name of the character is visible.
     *
     * @return {@code true} if the display name is visible, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isDisplayNameVisible();

    /**
     * Sets whether the display name of the character is visible.
     *
     * @param visible {@code true} if the display name should be visible, {@code false} otherwise
     * @return {@code true} if the visibility was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setDisplayNameVisible(boolean visible);

    /**
     * Returns the value of the given property.
     * <p>
     * If no value was set, the value of the character's entity is returned, if it is spawned.
     *
     * @param property the property to get
     * @param <T>      the value type of the property
     * @return the value of the given property
     * @see EntityProperties
     * @since 0.9.0
     */
    @Contract(pure = true)
    <T> Optional<T> getProperty(EntityProperty<?, T> property);

    /**
     * Sets the value of the given property.
     * <p>
     * The value is cached and persisted with the character
     * and applied to its entity immediately if it is spawned, or as soon as it spawns.
     *
     * @param property the property to set
     * @param value    the new value
     * @param <T>      the value type of the property
     * @return {@code true} if the value was changed, {@code false} if it is unchanged
     * or the property does not apply to the character's entity type
     * @see EntityProperty#isApplicable(Class)
     * @see EntityProperties
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    <T> boolean setProperty(EntityProperty<?, T> property, T value);

    /**
     * Returns the options of the character's name tag.
     *
     * @return tag options
     * @since 0.1.0
     */
    @Contract(pure = true)
    TagOptions getTagOptions();

    /**
     * Returns the team color of the character.
     *
     * @return team color
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<NamedTextColor> getTeamColor();

    /**
     * Sets the team color of the character.
     *
     * @param color new team color
     * @return {@code true} if the team color was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setTeamColor(@Nullable NamedTextColor color);

    /**
     * Returns the goals of the character.
     *
     * @return goals
     * @since 0.1.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<Goal> getGoals();

    /**
     * Adds a goal to the character.
     *
     * @param goal goal to add
     * @return {@code true} if the goal was added, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean addGoal(Goal goal);

    /**
     * Removes a goal from the character.
     *
     * @param goal goal to remove
     * @return {@code true} if the goal was removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeGoal(Goal goal);

    /**
     * Gets the click actions of this character.
     *
     * @return click actions
     * @since 0.1.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Map<String, ClickAction<?>> getActions();

    /**
     * Gets the click action with the given name.
     *
     * @param name action name
     * @return click action
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<ClickAction<?>> getAction(String name);

    /**
     * Checks if this character has the given click action.
     *
     * @param action click action
     * @return {@code true} if this character has the given click action, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean hasAction(ClickAction<?> action);

    /**
     * Checks if this character has a click action with the given name.
     *
     * @param name action name
     * @return {@code true} if this character has a click action with the given name, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean hasAction(String name);

    /**
     * Checks if this character has any click actions.
     *
     * @return {@code true} if this character has any click actions, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean hasActions();

    /**
     * Adds a click action to this character.
     * <p>
     * An existing action with the same name will be replaced.
     *
     * @param name   action name
     * @param action click action
     * @return {@code true} if the action was added, {@code false} otherwise
     * @see net.thenextlvl.character.action.ActionType#isApplicable(Object, Character)
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean addAction(String name, ClickAction<?> action);

    /**
     * Removes a click action from this character.
     *
     * @param name action name
     * @return {@code true} if the action was removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeAction(String name);

    /**
     * Iterates over all click actions of this character.
     *
     * @param action action consumer
     * @since 0.9.0
     */
    void forEachAction(BiConsumer<String, ? super ClickAction<?>> action);

    /**
     * Returns the view permission of this character.
     *
     * @return view permission
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<String> getViewPermission();

    /**
     * Sets the view permission of this character.
     *
     * @param permission view permission
     * @return {@code true} if the view permission was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setViewPermission(@Nullable String permission);

    /**
     * Returns the players that are tracking this character.
     *
     * @return players that are tracking this character
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Player> getTrackedBy();

    /**
     * Returns the viewers of this character.
     *
     * @return viewers
     * @since 0.1.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<UUID> getViewers();

    /**
     * Adds a viewer to this character.
     *
     * @param player viewer
     * @return {@code true} if the viewer was added, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean addViewer(UUID player);

    /**
     * Adds viewers to this character.
     *
     * @param players viewers
     * @return {@code true} if any viewers were added, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean addViewers(Collection<UUID> players);

    /**
     * Removes a viewer from this character.
     *
     * @param player viewer
     * @return {@code true} if the viewer was removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeViewer(UUID player);

    /**
     * Removes viewers from this character.
     *
     * @param players viewers
     * @return {@code true} if any viewers were removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeViewers(Collection<UUID> players);

    /**
     * Checks if this character has the given viewer.
     *
     * @param player viewer
     * @return {@code true} if this character has the given viewer, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isViewer(UUID player);

    /**
     * Checks if the given player can see this character.
     * <p>
     * This method is different to {@link #isTrackedBy(Player)} in that it checks
     * if the player has the permission to see the character.
     *
     * @param player player
     * @return {@code true} if the given player can see this character, {@code false} otherwise
     * @see #getViewPermission()
     * @see #isVisibleByDefault()
     * @see #isViewer(UUID)
     * @see #isTrackedBy(Player)
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean canSee(Player player);

    /**
     * Checks if this character is tracked by the given player.
     *
     * @param player the player to check for
     * @return {@code true} if this character is tracked by the given player, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isTrackedBy(Player player);

    /**
     * Checks if this character is visible by default.
     *
     * @return {@code true} if this character is visible by default, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isVisibleByDefault();

    /**
     * Sets whether this character is visible by default.
     *
     * @param visible {@code true} if this character should be visible by default, {@code false} otherwise
     * @return {@code true} if the visibility was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setVisibleByDefault(boolean visible);

    /**
     * Checks if this character is persistent.
     *
     * @return {@code true} if this character is persistent, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isPersistent();

    /**
     * Sets whether this character is persistent.
     *
     * @param persistent {@code true} if this character should be persistent, {@code false} otherwise
     * @return {@code true} if the persistence was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setPersistent(boolean persistent);

    /**
     * Persists this character.
     *
     * @return {@code true} if the character was persisted, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "io,this")
    boolean persist();

    /**
     * Returns the data file of this character.
     *
     * @return data file
     * @since 0.9.0
     */
    @Contract(pure = true)
    Path getDataFile();

    /**
     * Returns the backup file of this character.
     *
     * @return backup file
     * @since 0.9.0
     */
    @Contract(pure = true)
    Path getBackupFile();

    /**
     * Checks if this character is spawned.
     *
     * @return {@code true} if this character is spawned, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isSpawned();

    /**
     * Checks if the given entity is part of this character.
     * <p>
     * This includes the character's entity and its name tag.
     *
     * @param entity the entity to check for
     * @return {@code true} if the given entity is part of this character, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isPart(Entity entity);

    /**
     * Spawns this character.
     * <p>
     * Spawning only succeeds if the chunk at the character's location is loaded.
     * Characters are spawned automatically once their chunk loads.
     *
     * @return a {@link CompletableFuture} that completes with {@code true}
     * if the character was spawned, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> spawn();

    /**
     * Despawns this character.
     * <p>
     * This removes the character's entity from the world, but does not delete the character.
     *
     * @return a {@link CompletableFuture} that completes with {@code true}
     * if the character was despawned, {@code false} otherwise
     * @see CharacterProvider#deleteCharacter(Character)
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> despawn();
}
