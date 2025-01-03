package net.thenextlvl.character;

import net.kyori.adventure.text.Component;
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
    @Nullable
    ClickAction<?> getAction(String name);

    @Nullable
    Component getDisplayName();

    EntityType getType();

    @Nullable
    Location getLocation();

    @Nullable
    Location getSpawnLocation();

    @Unmodifiable
    Map<String, ClickAction<?>> getActions();

    Optional<T> getEntity();

    Pose getPose();

    @Unmodifiable
    Set<UUID> getViewers();

    String getName();

    @Nullable
    World getWorld();

    boolean addAction(String name, ClickAction<?> action);

    boolean addViewer(UUID player);

    boolean addViewers(Collection<UUID> players);

    boolean canSee(Player player);

    boolean despawn();

    boolean hasAction(ClickAction<?> action);

    boolean hasAction(String name);

    boolean isCollidable();

    boolean isDisplayNameVisible();

    boolean isInvincible();

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

    boolean spawn();

    boolean spawn(Location location);

    void remove();

    void setCollidable(boolean collidable);

    void setDisplayName(@Nullable Component displayName);

    void setDisplayNameVisible(boolean visible);

    void setInvincible(boolean invincible);

    void setPersistent(boolean persistent);

    void setPose(Pose pose);

    void setSpawnLocation(@Nullable Location location);

    void setVisibleByDefault(boolean visible);
}
