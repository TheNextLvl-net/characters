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

    void setDisplayName(@Nullable Component displayName);

    Optional<T> getEntity();

    @Nullable
    NamedTextColor getGlowColor();

    void setGlowColor(@Nullable NamedTextColor color);

    @Nullable
    Location getLocation();

    String getName();

    Pose getPose();

    void setPose(Pose pose);

    @Nullable
    Location getSpawnLocation();

    void setSpawnLocation(@Nullable Location location);

    EntityType getType();

    @Unmodifiable
    Set<UUID> getViewers();

    @Nullable
    World getWorld();

    boolean hasAction(ClickAction<?> action);

    boolean hasAction(String name);

    boolean hasGravity();

    boolean isCollidable();

    void setCollidable(boolean collidable);

    boolean isDisplayNameVisible();

    void setDisplayNameVisible(boolean visible);

    boolean isGlowing();

    void setGlowing(boolean glowing);

    boolean isInvincible();

    void setInvincible(boolean invincible);

    boolean isPersistent();

    void setPersistent(boolean persistent);

    boolean isSpawned();

    boolean isTicking();

    void setTicking(boolean ticking);

    boolean isTrackedBy(Player player);

    boolean isViewer(UUID player);

    boolean isVisibleByDefault();

    void setVisibleByDefault(boolean visible);

    boolean persist();

    void remove();

    boolean removeAction(String name);

    boolean removeViewer(UUID player);

    boolean removeViewers(Collection<UUID> players);

    boolean respawn();

    boolean respawn(Location location);

    void setGravity(boolean gravity);

    boolean spawn();

    boolean spawn(Location location);
}
