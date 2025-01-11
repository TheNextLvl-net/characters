package net.thenextlvl.character.plugin.character;

import com.google.common.base.Preconditions;
import core.io.IO;
import core.nbt.NBTOutputStream;
import net.kyori.adventure.text.Component;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.model.EmptyLootTable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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

    protected Pose pose = Pose.STANDING;

    protected boolean collidable = false;
    protected boolean displayNameVisible = true;
    protected boolean invincible = true;
    protected boolean persistent = true;
    protected boolean visibleByDefault = true;

    protected final EntityType type;
    protected final Map<String, ClickAction<?>> actions = new HashMap<>();
    protected final Set<UUID> viewers = new HashSet<>();
    protected final String name;

    protected final CharacterPlugin plugin;

    public PaperCharacter(CharacterPlugin plugin, String name, EntityType type) {
        this.name = name;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public ClickAction<?> getAction(String name) {
        return actions.get(name);
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
    public @Unmodifiable Map<String, ClickAction<?>> getActions() {
        return Map.copyOf(actions);
    }

    @Override
    public Optional<T> getEntity() {
        return Optional.ofNullable(entity).filter(Entity::isValid);
    }

    @Override
    public Pose getPose() {
        return pose;
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
    public boolean addAction(String name, ClickAction<?> action) {
        return !action.equals(actions.put(name, action));
    }

    @Override
    public boolean addViewer(UUID player) {
        if (!viewers.add(player)) return false;
        if (entity == null || isVisibleByDefault()) return true;
        var online = plugin.getServer().getPlayer(player);
        if (online != null) online.showEntity(plugin, entity);
        return true;
    }

    @Override
    public boolean addViewers(Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean canSee(Player player) {
        if (entity == null || !isSpawned()) return false;
        if (!player.getWorld().equals(entity.getWorld())) return false;
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
    public boolean hasAction(ClickAction<?> action) {
        return actions.containsValue(action);
    }

    @Override
    public boolean hasAction(String name) {
        return actions.containsKey(name);
    }

    @Override
    public boolean isCollidable() {
        return collidable;
    }

    @Override
    public boolean isDisplayNameVisible() {
        return displayNameVisible;
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
        if (!isPersistent()) return false;
        var file = IO.of(file());
        var backup = IO.of(backupFile());
        try {
            if (file.exists()) Files.move(file.getPath(), backup.getPath(), StandardCopyOption.REPLACE_EXISTING);
            else file.createParents();
            try (var outputStream = new NBTOutputStream(
                    file.outputStream(WRITE, CREATE, TRUNCATE_EXISTING),
                    StandardCharsets.UTF_8
            )) {
                outputStream.writeTag(getName(), plugin.nbt().toTag(this));
                return true;
            }
        } catch (Throwable t) {
            if (backup.exists()) try {
                Files.copy(backup.getPath(), file.getPath(), StandardCopyOption.REPLACE_EXISTING);
                plugin.getComponentLogger().warn("Recovered {} from potential data loss", getName());
            } catch (IOException e) {
                plugin.getComponentLogger().error("Failed to restore character {}", getName(), e);
            }
            plugin.getComponentLogger().error("Failed to save character {}", getName(), t);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}",
                    CharacterPlugin.ISSUES);
            return false;
        }
    }

    @Override
    public boolean removeAction(String name) {
        return actions.remove(name) != null;
    }

    @Override
    public boolean removeViewer(UUID player) {
        if (!viewers.remove(player)) return false;
        if (entity == null || isVisibleByDefault()) return true;
        var online = plugin.getServer().getPlayer(player);
        if (online != null) online.hideEntity(plugin, entity);
        return true;
    }

    @Override
    public boolean removeViewers(Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
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
        Preconditions.checkNotNull(type.getEntityClass(), "Cannot spawn entity of type %s", type);
        this.entity = (T) location.getWorld().spawn(location, type.getEntityClass(), this::preSpawn);
        return true;
    }

    protected void preSpawn(Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.setAI(false);
            living.setCanPickupItems(false);
            living.setCollidable(isCollidable());
        }
        if (entity instanceof Mob mob) {
            mob.setLootTable(EmptyLootTable.INSTANCE);
        }
        entity.setInvulnerable(isInvincible());
        entity.setPersistent(false);
        entity.setPose(getPose(), true);
        entity.setSilent(true);
        entity.setVisibleByDefault(isVisibleByDefault());
        updateDisplayName(entity);
    }

    protected void updateDisplayName(Entity entity) {
        entity.customName(displayNameVisible ? displayName : null);
        entity.setCustomNameVisible(displayNameVisible && displayName != null);
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void remove() {
        despawn();
        file().delete();
        backupFile().delete();
        plugin.characterController().unregister(name);
    }

    @Override
    public void setCollidable(boolean collidable) {
        if (collidable == this.collidable) return;
        this.collidable = collidable;
        getEntity().ifPresent(entity -> {
            if (!(entity instanceof LivingEntity living)) return;
            living.setCollidable(collidable);
        });
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        if (displayName == this.displayName) return;
        this.displayName = displayName;
        // todo implement for players and add holo based display names
        getEntity().ifPresent(this::updateDisplayName);
    }

    @Override
    public void setDisplayNameVisible(boolean visible) {
        if (visible == displayNameVisible) return;
        this.displayNameVisible = visible;
        getEntity().ifPresent(this::updateDisplayName);
    }

    @Override
    public void setInvincible(boolean invincible) {
        if (invincible == this.invincible) return;
        this.invincible = invincible;
        getEntity().ifPresent(entity -> entity.setInvulnerable(invincible));
    }

    @Override
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public void setPose(Pose pose) {
        if (pose == this.pose) return;
        this.pose = pose;
        getEntity().ifPresent(entity -> entity.setPose(pose, true));
    }

    @Override
    public void setSpawnLocation(@Nullable Location location) {
        this.spawnLocation = location;
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        if (visible == visibleByDefault) return;
        this.visibleByDefault = visible;
        getEntity().ifPresent(entity -> {
            entity.setVisibleByDefault(visible);
            if (visible) entity.getTrackedBy().forEach(player -> {
                if (isViewer(player.getUniqueId())) return;
                player.hideEntity(plugin, entity);
            });
            else getViewers().stream().map(plugin.getServer()::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(player -> player.showEntity(plugin, entity));
        });
    }

    private File backupFile() {
        return new File(plugin.savesFolder(), this.name + ".dat_old");
    }

    private File file() {
        return new File(plugin.savesFolder(), this.name + ".dat");
    }
}
