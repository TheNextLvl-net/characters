package net.thenextlvl.character.model;

import com.google.common.base.Preconditions;
import core.io.IO;
import core.nbt.NBTOutputStream;
import net.kyori.adventure.text.Component;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@NullMarked
public class PaperCharacter<T extends Entity> implements Character<T> {
    protected @Nullable Component displayName = null;
    protected @Nullable Location spawnLocation = null;
    protected @Nullable T entity;

    protected boolean collidable = false;
    protected boolean invincible = true;
    protected boolean persistent = true;
    protected boolean visibleByDefault = true;

    protected final EntityType type;
    protected final File file;
    protected final Set<UUID> viewers = new HashSet<>();
    protected final String name;

    protected final CharacterPlugin plugin;

    public PaperCharacter(CharacterPlugin plugin, String name, EntityType type) {
        this.file = new File(plugin.savesFolder(), name + ".dat");
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
    public @Nullable Location getSpawnLocation() {
        return spawnLocation;
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
    // todo: can be removed entirely?
    public boolean canSee(Player player) {
        if (entity == null || !isSpawned()) return false;
        if (!player.getWorld().equals(entity.getWorld())) return false;
        if (!((CraftEntity) entity).getHandleRaw().shouldRender(player.getX(), player.getY(), player.getZ()))
            return false;
        return isVisibleByDefault() || isViewer(player.getUniqueId());
    }

    @Override
    public boolean despawn() {
        if (entity == null) return false;
        entity.remove();
        entity = null;
        return true;
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
        try {
            if (!isPersistent()) return false;
            var io = IO.of(file);
            if (io.exists()) Files.move(file.toPath(),
                    new File(file.getPath() + "_old").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            else io.createParents();
            try (var outputStream = new NBTOutputStream(
                    io.outputStream(WRITE, CREATE, TRUNCATE_EXISTING),
                    StandardCharsets.UTF_8
            )) {
                outputStream.writeTag(getName(), plugin.nbt().toTag(this));
                return true;
            }
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to save character {}", getName(), e);
            plugin.getComponentLogger().error("Please report this issue on GitHub: {}", CharacterPlugin.ISSUES);
            return false;
        }
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
        return spawnLocation != null && respawn(spawnLocation);
    }

    @Override
    public boolean respawn(Location location) {
        return despawn() && spawn(location);
    }

    @Override
    public boolean spawn() {
        return spawnLocation != null && spawn(spawnLocation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean spawn(Location location) {
        if (isSpawned()) return false;
        this.spawnLocation = location;
        Preconditions.checkNotNull(type.getEntityClass(), "Cannot spawn entity of type" + type);
        this.entity = (T) location.getWorld().spawn(location, type.getEntityClass(), this::preSpawn);
        return true;
    }

    protected void preSpawn(Entity entity) {
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
        entity.setInvulnerable(isInvincible());
        entity.setPersistent(isPersistent());
        entity.setSilent(true);
        entity.setVisibleByDefault(isVisibleByDefault());
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void remove() {
        despawn();
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
    public void setSpawnLocation(@Nullable Location location) {
        this.spawnLocation = location;
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        this.visibleByDefault = visible;
        getEntity().ifPresent(entity -> entity.setVisibleByDefault(visible));
    }
}
