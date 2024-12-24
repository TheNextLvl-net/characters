package net.thenextlvl.character;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * An interface that represents a npc
 */
@NullMarked
public interface Character<T extends Entity> {
    CompletableFuture<Boolean> teleportAsync(Location location);

    Component getDisplayName();

    EntityType getType();

    @Nullable
    Location getLocation();

    Optional<T> getEntity();

    Set<Player> getViewers();

    String getName();

    @Nullable
    World getWorld();

    boolean addViewer(Player player);

    boolean addViewers(Collection<Player> players);

    boolean canSee(Player player);

    boolean despawn();

    boolean isCollidable();

    boolean isInvulnerable();

    boolean isPersistent();

    boolean isSpawned();

    boolean isTrackedBy(Player player);

    boolean isVisibleByDefault();

    boolean persist();

    boolean removeViewer(Player player);

    boolean removeViewers(Collection<Player> players);

    boolean respawn();

    boolean spawn(Location location);

    double getDisplayRange();

    int getLoadingRange();

    void lookAt(Entity entity);

    void lookAt(Location location);

    void remove();

    void setCollidable(boolean collidable);

    void setDisplayName(Component displayName);

    void setDisplayRange(double range);

    void setInvulnerable(boolean invulnerable);

    void setPersistent(boolean persistent);

    void setVisibleByDefault(boolean visible);
}
