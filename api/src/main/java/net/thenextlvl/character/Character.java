package net.thenextlvl.character;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.property.EntityProperties;
import net.thenextlvl.character.property.EntityProperty;
import net.thenextlvl.character.property.ViewerValue;
import net.thenextlvl.character.property.ViewerValues;
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
import java.util.List;
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
     * Returns the entity of the character that is shown to the given player.
     * <p>
     * Every player that can see the character has their own entity, which is only visible to them.
     * The entity only exists while the character is spawned for the player,
     * which requires its chunk to be loaded and sent to the player.
     * Changes made directly to the entity are not persisted.
     *
     * @param player the player to get the entity for
     * @return the entity, or empty if the character is not spawned for the player
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Entity> getEntity(Player player);

    /**
     * Returns the entity of the character that is shown to the given player if it is of the given type.
     *
     * @param player the player to get the entity for
     * @param type   entity type
     * @param <T>    entity type
     * @return the entity, or empty if the character is not spawned for the player or not of the given type
     * @since 0.9.0
     */
    @Contract(pure = true)
    <T> Optional<T> getEntity(Player player, Class<T> type);

    /**
     * Returns the entities of the character, one per player it is spawned for.
     *
     * @return the entities of the character
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Entity> getEntities();

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
     * Returns the current location of the entity that is shown to the given player.
     * <p>
     * Goals move every entity on its own, so this may differ from {@link #getLocation()}
     * and between players.
     *
     * @param player the player to get the location for
     * @return the location of the entity, or empty if the character is not spawned for the player
     * @see #getGoals()
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Location> getLocation(Player player);

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
     * The new location is applied immediately, the entities are moved once they exist.
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
     * Returns the unparsed display name of the character.
     * <p>
     * The display name is in the <a href="https://docs.papermc.io/adventure/minimessage/format">MiniMessage format</a>
     * and parsed separately for every viewer, see {@link #getDisplayName(Player)}.
     *
     * @return unparsed display name
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<String> getDisplayName();

    /**
     * Returns the display name of the character as the given viewer sees it.
     * <p>
     * PlaceholderAPI placeholders are applied first if PlaceholderAPI is installed,
     * the result is then parsed as MiniMessage with the following tags:
     * <ul>
     *     <li>the standard MiniMessage tags</li>
     *     <li>MiniPlaceholders tags, if MiniPlaceholders is installed</li>
     *     <li>{@code <character>}: the name of the character</li>
     *     <li>{@code <player>}: the name of the viewer</li>
     *     <li>{@code <players>}: the number of online players</li>
     * </ul>
     *
     * @param viewer the viewer
     * @return parsed display name
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Component> getDisplayName(Player viewer);

    /**
     * Sets the unparsed display name of the character.
     *
     * @param displayName new display name in the MiniMessage format
     * @return {@code true} if the display name was changed, {@code false} otherwise
     * @see #getDisplayName(Player)
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean setDisplayName(@Nullable String displayName);

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
     * Returns the persisted value of the given property.
     * <p>
     * The state of the character's entities is never taken into account.
     *
     * @param property the property to get
     * @param <T>      the value type of the property
     * @return the persisted value of the given property, or empty if no value was set
     * @see EntityProperties
     * @since 0.9.0
     */
    @Contract(pure = true)
    <T> Optional<T> getProperty(EntityProperty<?, T> property);

    /**
     * Sets the value of the given property.
     * <p>
     * The value is cached and persisted with the character
     * and applied to its entities immediately if it is spawned, or as soon as it spawns.
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
     * Returns the value of the given property as the given viewer sees it.
     * <p>
     * A {@link #getViewerValue(EntityProperty) viewer value} takes precedence over the
     * {@link #getProperty(EntityProperty) persisted value}.
     *
     * @param property the property to get
     * @param viewer   the viewer to resolve the value for
     * @param <T>      the value type of the property
     * @return the value the given viewer sees, or empty if no value was set
     * @since 0.9.0
     */
    @Contract(pure = true)
    <T> Optional<T> getProperty(EntityProperty<?, T> property, Player viewer);

    /**
     * Returns the viewer value of the given property.
     *
     * @param property the property to get the viewer value for
     * @param <T>      the value type of the property
     * @return the viewer value, or empty if none is set or it is not registered
     * @see ViewerValues
     * @since 0.9.0
     */
    @Contract(pure = true)
    <T> Optional<ViewerValue<T>> getViewerValue(EntityProperty<?, T> property);

    /**
     * Sets the viewer value of the given property.
     * <p>
     * The value is resolved separately for every viewer and takes precedence over the
     * {@link #getProperty(EntityProperty) persisted value}.
     * The reference to the viewer value is persisted with the character.
     * Removing the viewer value restores the persisted value.
     *
     * @param property the property to set the viewer value for
     * @param value    the viewer value, or {@code null} to remove it
     * @param <T>      the value type of the property
     * @return {@code true} if the viewer value was changed, {@code false} if it is unchanged
     * or the property does not apply to the character's entity type
     * @see ViewerValues
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    <T> boolean setViewerValue(EntityProperty<?, T> property, @Nullable ViewerValue<T> value);

    /**
     * Returns the options of the character's name tag.
     *
     * @return tag options
     * @since 0.1.0
     */
    // todo: replace with Holograms API
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
     * Returns the goals of the character, in the order they take turns.
     *
     * @return goals
     * @see Goal
     * @since 0.9.0
     */
    @Unmodifiable
    @Contract(pure = true)
    List<Goal> getGoals();

    /**
     * Adds a goal to the end of the goals of the character.
     *
     * @param goal goal to add
     * @return {@code true} if the goal was added, {@code false} if the character already has it
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean addGoal(Goal goal);

    /**
     * Adds a goal to the character at the given position in the order goals take turns.
     *
     * @param index the position, {@code 0} being the first
     * @param goal  goal to add
     * @return {@code true} if the goal was added, {@code false} if the character already has it
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean addGoal(int index, Goal goal) throws IndexOutOfBoundsException;

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
     * Removes all goals from the character.
     *
     * @return {@code true} if any goal was removed, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean clearGoals();

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
     * Returns the players this character is spawned for.
     *
     * @return players this character is spawned for
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
     * Checks if this character is spawned for the given player.
     *
     * @param player the player to check for
     * @return {@code true} if this character is spawned for the given player, {@code false} otherwise
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
     * Checks if this character is spawned for any player.
     *
     * @return {@code true} if this character is spawned for any player, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isSpawned();

    /**
     * Checks if this character is spawned for the given player.
     *
     * @param player the player to check for
     * @return {@code true} if this character is spawned for the given player, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isSpawned(Player player);

    /**
     * Checks if the given entity is part of this character.
     * <p>
     * This includes the character's entities and its name tag.
     *
     * @param entity the entity to check for
     * @return {@code true} if the given entity is part of this character, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isPart(Entity entity);

    /**
     * Spawns this character for all players that can see it and have its chunk loaded.
     * <p>
     * Spawning only succeeds if the chunk at the character's location is loaded.
     * Characters are spawned automatically once their chunk is sent to a player.
     *
     * @return a {@link CompletableFuture} that completes with {@code true}
     * if the character was spawned for any player, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> spawn();

    /**
     * Spawns this character for the given player.
     * <p>
     * Spawning only succeeds if the player can see the character
     * and the chunk at the character's location is loaded.
     *
     * @param player the player to spawn the character for
     * @return a {@link CompletableFuture} that completes with {@code true}
     * if the character was spawned for the player, {@code false} otherwise
     * @see #canSee(Player)
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> spawn(Player player);

    /**
     * Despawns this character for all players.
     * <p>
     * This removes the character's entities from the world, but does not delete the character.
     *
     * @return a {@link CompletableFuture} that completes with {@code true}
     * if the character was despawned, {@code false} otherwise
     * @see CharacterProvider#deleteCharacter(Character)
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> despawn();

    /**
     * Despawns this character for the given player.
     * <p>
     * The name tag is removed along with the last entity of the character.
     *
     * @param player the player to despawn the character for
     * @return a {@link CompletableFuture} that completes with {@code true}
     * if the character was despawned for the player, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    CompletableFuture<Boolean> despawn(Player player);
}
