package net.thenextlvl.character.plugin.character;

import com.google.common.base.Preconditions;
import core.io.IO;
import core.nbt.NBTOutputStream;
import core.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.model.EmptyLootTable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
    protected final Map<String, ClickAction<?>> actions = new LinkedHashMap<>();
    protected final Set<UUID> viewers = new HashSet<>();
    protected final String scoreboardName = StringUtil.random(32);

    protected final CharacterPlugin plugin;
    protected final EntityType type;
    protected final String name;

    protected @Nullable Component displayName = null;
    protected @Nullable Location spawnLocation = null;
    protected @Nullable NamedTextColor teamColor = null;
    protected @Nullable T entity;

    protected Pose pose = Pose.STANDING;

    protected boolean ai = false;
    protected boolean collidable = false;
    protected boolean displayNameVisible = true;
    protected boolean glowing = false;
    protected boolean gravity = false;
    protected boolean invincible = true;
    protected boolean pathfinding = false;
    protected boolean persistent = true;
    protected boolean ticking = false;
    protected boolean visibleByDefault = true;

    protected double scale = 1;

    public PaperCharacter(CharacterPlugin plugin, String name, EntityType type) {
        this.name = name;
        this.plugin = plugin;
        this.type = type;
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
    public ClickAction<?> getAction(String name) {
        return actions.get(name);
    }

    @Override
    public @Unmodifiable Map<String, ClickAction<?>> getActions() {
        return Map.copyOf(actions);
    }

    @Override
    public @Nullable Component getDisplayName() {
        return displayName;
    }

    @Override
    public <V> Optional<V> getEntity(Class<V> type) {
        return getEntity().filter(type::isInstance).map(type::cast);
    }

    @Override
    public Optional<T> getEntity() {
        return Optional.ofNullable(entity).filter(Entity::isValid);
    }

    @Override
    public @Nullable NamedTextColor getTeamColor() {
        return teamColor;
    }

    @Override
    public @Nullable Location getLocation() {
        return getEntity().map(Entity::getLocation).orElse(null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getScoreboardName() {
        return scoreboardName;
    }

    @Override
    public Pose getPose() {
        return pose;
    }

    @Override
    public @Nullable Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }

    @Override
    public @Nullable World getWorld() {
        return getEntity().map(Entity::getWorld).orElse(null);
    }

    @Override
    public boolean hasAI() {
        return ai;
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
    public boolean hasGravity() {
        return gravity;
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
    public boolean isGlowing() {
        return glowing;
    }

    @Override
    public boolean isInvincible() {
        return invincible;
    }

    @Override
    public boolean isPathfinding() {
        return pathfinding;
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
    public boolean isTicking() {
        return ticking;
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
    public boolean setAI(boolean ai) {
        if (ai == this.ai) return false;
        getEntity(LivingEntity.class).ifPresent(entity -> entity.setAI(ai));
        this.ai = ai;
        return true;
    }

    @Override
    public boolean setCollidable(boolean collidable) {
        if (collidable == this.collidable) return false;
        getEntity(LivingEntity.class).ifPresent(entity -> entity.setCollidable(collidable));
        this.collidable = collidable;
        return true;
    }

    @Override
    public boolean setDisplayName(@Nullable Component displayName) {
        if (Objects.equals(displayName, this.displayName)) return false;
        this.displayName = displayName;
        getEntity().ifPresent(this::updateDisplayName);
        return true;
    }

    @Override
    public boolean setDisplayNameVisible(boolean visible) {
        if (visible == displayNameVisible) return false;
        this.displayNameVisible = visible;
        getEntity().ifPresent(this::updateDisplayName);
        return true;
    }

    @Override
    public boolean setGlowing(boolean glowing) {
        if (glowing == this.glowing) return false;
        getEntity().ifPresent(entity -> entity.setGlowing(glowing));
        this.glowing = glowing;
        return true;
    }

    @Override
    public boolean setGravity(boolean gravity) {
        if (gravity == this.gravity) return false;
        getEntity().ifPresent(entity -> entity.setGravity(gravity));
        this.gravity = gravity;
        return true;
    }

    @Override
    public boolean setInvincible(boolean invincible) {
        if (invincible == this.invincible) return false;
        getEntity().ifPresent(entity -> entity.setInvulnerable(invincible));
        this.invincible = invincible;
        return true;
    }

    @Override
    public boolean setPathfinding(boolean pathfinding) {
        if (pathfinding == this.pathfinding) return false;
        this.pathfinding = pathfinding;
        getEntity(Mob.class).ifPresent(this::updatePathfinderGoals);
        return true;
    }

    @Override
    public boolean setPersistent(boolean persistent) {
        if (persistent == this.persistent) return false;
        this.persistent = persistent;
        return true;
    }

    @Override
    public boolean setPose(Pose pose) {
        if (pose == this.pose) return false;
        getEntity().ifPresent(entity -> entity.setPose(pose, true));
        this.pose = pose;
        return true;
    }

    @Override
    public boolean setScale(double scale) {
        if (scale == this.scale) return false;
        getEntity(Attributable.class).map(instance -> instance.getAttribute(Attribute.SCALE))
                .ifPresent(attribute -> attribute.setBaseValue(scale));
        this.scale = scale;
        return true;
    }

    @Override
    public boolean setSpawnLocation(@Nullable Location location) {
        if (Objects.equals(location, spawnLocation)) return false;
        this.spawnLocation = location;
        return true;
    }

    @Override
    public boolean setTeamColor(@Nullable NamedTextColor color) {
        if (color == this.teamColor) return false;
        this.teamColor = color;
        return true;
    }

    @Override
    public boolean setTicking(boolean ticking) {
        if (ticking == this.ticking) return false;
        this.ticking = ticking;
        // todo: use custom entity impl to make this possible
        return true;
    }

    @Override
    public boolean setVisibleByDefault(boolean visible) {
        if (visible == visibleByDefault) return false;
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
        return true;
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
        this.entity = location.getWorld().spawn(location, (Class<T>) type.getEntityClass(), this::preSpawn);
        return true;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void delete() {
        remove();
        backupFile().delete();
        file().delete();
        plugin.characterController().unregister(name);
    }

    @Override
    public void remove() {
        despawn();
    }

    protected void preSpawn(T entity) {
        if (entity instanceof Attributable attributable) {
            var scale = attributable.getAttribute(Attribute.SCALE);
            if (scale != null) scale.setBaseValue(this.scale);
        }
        if (entity instanceof LivingEntity living) {
            living.setAI(ai);
            living.setCanPickupItems(false);
            living.setCollidable(collidable);
        }
        if (entity instanceof Mob mob) {
            mob.setLootTable(EmptyLootTable.INSTANCE);
            updatePathfinderGoals(mob);
        }
        entity.setGlowing(glowing);
        entity.setGravity(gravity);
        entity.setInvulnerable(invincible);
        entity.setMetadata("NPC", new FixedMetadataValue(plugin, true));
        entity.setPersistent(false);
        entity.setPose(pose, true);
        entity.setSilent(true);
        entity.setVisibleByDefault(visibleByDefault);
        updateDisplayName(entity);
    }

    protected void updateDisplayName(T entity) {
        entity.customName(displayNameVisible ? displayName : null);
        entity.setCustomNameVisible(displayNameVisible && displayName != null);
    }

    protected void updatePathfinderGoals(Mob mob) {
        if (!pathfinding) plugin.getServer().getMobGoals().removeAllGoals(mob);
    }

    private File backupFile() {
        return new File(plugin.savesFolder(), this.name + ".dat_old");
    }

    private File file() {
        return new File(plugin.savesFolder(), this.name + ".dat");
    }
}
