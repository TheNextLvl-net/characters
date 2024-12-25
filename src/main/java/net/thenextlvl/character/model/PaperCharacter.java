package net.thenextlvl.character.model;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.loot.Lootable;
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

    private boolean collidable = false;
    private boolean invincible = true;
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
        return Optional.ofNullable(entity).filter(Entity::isValid);
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
        // todo: check if player is in range

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
    public boolean isInvincible() {
        return invincible;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public boolean isSpawned() {
        return entity != null && entity.isValid();
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
        return false; // todo: persist
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
    @SuppressWarnings("unchecked")
    public boolean spawn(Location location) {
        if (isSpawned()) return false;
        Preconditions.checkNotNull(type.getEntityClass(), "Cannot spawn entity of type" + type);
        this.entity = (T) location.getWorld().spawn(location, type.getEntityClass(), entity -> {
            if (entity instanceof LivingEntity living) {
                living.setAI(false);
                living.setCanPickupItems(false);
                living.setCollidable(isCollidable());
            }
            if (entity instanceof Lootable lootable) {
                lootable.clearLootTable();
            }
            entity.customName(Optional.ofNullable(getDisplayName())
                    .orElseGet(() -> Component.text(getName())));
            entity.setCustomNameVisible(true);
            entity.setGravity(false);
            entity.setInvulnerable(isInvincible());
            entity.setPersistent(isPersistent());
            entity.setSilent(true);
            entity.setVisibleByDefault(isVisibleByDefault());
        });
        return true;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void remove() {
        getEntity().ifPresent(Entity::remove);
        if (!isPersistent()) file.delete();
        plugin.characterController().unregister(name);
    }

    @Override
    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
        getEntity().ifPresent(entity -> {
            if (!(entity instanceof LivingEntity living)) return;
            living.setCollidable(collidable);
        });
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
        getEntity().ifPresent(entity -> entity.customName(displayName));
    }

    @Override
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
        getEntity().ifPresent(entity -> entity.setInvulnerable(invincible));
    }

    @Override
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
        getEntity().ifPresent(entity -> entity.setPersistent(persistent));
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        this.visibleByDefault = visible;
        getEntity().ifPresent(entity -> entity.setVisibleByDefault(visible));
    }
}
