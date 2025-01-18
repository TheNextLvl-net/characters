package net.thenextlvl.character;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.character.action.ClickAction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NullMarked
public interface Character<T extends Entity> {
    boolean addAction(String name, ClickAction<?> action);

    boolean addViewer(UUID player);

    boolean addViewers(Collection<UUID> players);

    boolean canSee(Player player);

    boolean despawn();

    @Nullable
    ClickAction<?> getAction(String name);

    @Unmodifiable
    Map<String, ClickAction<?>> getActions();

    @Nullable
    Component getDisplayName();

    <V> Optional<V> getEntity(Class<V> type);

    Optional<T> getEntity();

    @Nullable
    NamedTextColor getGlowColor();

    @Nullable
    Location getLocation();

    String getName();

    String getScoreboardName();

    Pose getPose();

    @Nullable
    Location getSpawnLocation();

    EntityType getType();

    @Unmodifiable
    Set<UUID> getViewers();

    @Nullable
    World getWorld();

    boolean hasAI();

    boolean hasAction(ClickAction<?> action);

    boolean hasAction(String name);

    boolean hasGravity();

    boolean isCollidable();

    boolean isDisplayNameVisible();

    boolean isGlowing();

    boolean isInvincible();

    boolean isPathfinding();

    boolean isPersistent();

    boolean isSpawned();

    boolean isTicking();

    boolean isTrackedBy(Player player);

    boolean isViewer(UUID player);

    boolean isVisibleByDefault();

    boolean persist();

    void remove();

    boolean removeAction(String name);

    boolean removeViewer(UUID player);

    boolean removeViewers(Collection<UUID> players);

    boolean respawn();

    boolean respawn(Location location);

    boolean setAI(boolean ai);

    boolean setCollidable(boolean collidable);

    boolean setDisplayName(@Nullable Component displayName);

    boolean setDisplayNameVisible(boolean visible);

    boolean setGlowColor(@Nullable NamedTextColor color);

    boolean setGlowing(boolean glowing);

    boolean setGravity(boolean gravity);

    boolean setInvincible(boolean invincible);

    boolean setPathfinding(boolean pathfinding);

    boolean setPersistent(boolean persistent);

    boolean setPose(Pose pose);

    boolean setSpawnLocation(@Nullable Location location);

    boolean setTicking(boolean ticking);

    boolean setVisibleByDefault(boolean visible);

    boolean spawn();

    boolean spawn(Location location);
}
