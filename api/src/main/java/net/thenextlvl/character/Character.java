package net.thenextlvl.character;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NullMarked
public interface Character<T extends Entity> {
    @Nullable
    Component getDisplayName();

    EntityType getType();

    @Nullable
    Location getLocation();

    @Nullable
    Location getSpawnLocation();

    Optional<T> getEntity();

    @Unmodifiable
    Set<UUID> getViewers();

    String getName();

    @Nullable
    World getWorld();

    boolean addViewer(UUID player);

    boolean addViewers(Collection<UUID> players);

    boolean canSee(Player player);

    boolean despawn();

    boolean isCollidable();

    boolean isInvincible();

    boolean isPersistent();

    boolean isSpawned();

    boolean isTrackedBy(Player player);

    boolean isViewer(UUID player);

    boolean isVisibleByDefault();

    boolean persist();

    boolean removeViewer(UUID player);

    boolean removeViewers(Collection<UUID> players);

    boolean respawn();

    boolean respawn(Location location);

    boolean spawn();

    boolean spawn(Location location);

    void remove();

    void setCollidable(boolean collidable);

    void setDisplayName(@Nullable Component displayName);

    void setInvincible(boolean invincible);

    void setPersistent(boolean persistent);

    void setSpawnLocation(@Nullable Location location);

    void setVisibleByDefault(boolean visible);
}
