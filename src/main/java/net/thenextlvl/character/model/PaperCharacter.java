package net.thenextlvl.character.model;

import net.kyori.adventure.text.Component;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class PaperCharacter<T extends Entity> implements Character<T> {
    private @Nullable Component displayName = null;
    private @Nullable T entity;

    private boolean collidable = true;
    private boolean persistent = true;
    private boolean visibleByDefault = true;

    private final EntityType type;
    private final File file;
    private final Set<UUID> viewers = new HashSet<>();
    private final String name;

    protected final CharacterPlugin plugin;

    public PaperCharacter(CharacterPlugin plugin, String name, EntityType type) {
        this.file = new File(plugin.getDataFolder(), name + ".dat");
        this.name = name;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public @Nullable Component getDisplayName() {
        return displayName;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public @Nullable Location getLocation() {
        return getEntity().map(Entity::getLocation).orElse(null);
    }

    @Override
    public Optional<T> getEntity() {
        return Optional.ofNullable(entity);
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public @Nullable World getWorld() {
        return getEntity().map(Entity::getWorld).orElse(null);
    }

    @Override
    public boolean addViewer(UUID player) {
        return viewers.add(player);
    }

    @Override
    public boolean addViewers(Collection<UUID> players) {
        return viewers.addAll(players);
    }

    @Override
    public boolean canSee(Player player) {
        if (!isSpawned()) return false;

        var location = getLocation();
        if (location == null || !player.getWorld().equals(location.getWorld())) return false;
        //if (player.getLocation().distanceSquared(location) > 16 * 16) return false;

        return isVisibleByDefault() || isViewer(player.getUniqueId());
    }

    @Override
    public boolean despawn() {
        return getEntity().map(entity -> {
            entity.remove();
            return true;
        }).orElse(false);
    }

    @Override
    public boolean isCollidable() {
        return collidable;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public boolean isSpawned() {
        return entity != null;
    }

    @Override
    public boolean isTrackedBy(Player player) {
        return getEntity().map(entity -> entity.getTrackedBy().contains(player)).orElse(false);
    }

    @Override
    public boolean isViewer(UUID player) {
        return viewers.contains(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    @Override
    public boolean persist() {
        return false;
    }

    @Override
    public boolean removeViewer(UUID player) {
        return viewers.remove(player);
    }

    @Override
    public boolean removeViewers(Collection<UUID> players) {
        return viewers.removeAll(players);
    }

    @Override
    public boolean respawn() {
        var location = getLocation();
        return location != null && despawn() && spawn(location);
    }

    @Override
    public boolean spawn(Location location) {
        if (isSpawned() || type.getEntityClass() == null) return false;
        location.getWorld().spawn(location, type.getEntityClass(), entity -> {
            if (entity instanceof LivingEntity living) {
                living.setAI(false);
                living.setCanPickupItems(false);
                living.setCollidable(isCollidable());
                living.setInvulnerable(true);
                living.setSilent(true);
            }
            entity.customName(getDisplayName());
            entity.setCustomNameVisible(getDisplayName() != null);
            entity.setPersistent(isPersistent());
            entity.setVisibleByDefault(isVisibleByDefault());
        });
        return true;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void remove() {
        file.delete();
        despawn();
    }

    @Override
    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
    }

    @Override
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        this.visibleByDefault = visible;
    }
}
