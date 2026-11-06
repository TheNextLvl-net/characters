package net.thenextlvl.character.plugin.character;

import com.google.common.base.Preconditions;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.event.CharacterTeleportEvent;
import net.thenextlvl.character.event.CharacterViewerAddEvent;
import net.thenextlvl.character.event.CharacterViewerRemoveEvent;
import net.thenextlvl.character.goal.FollowGoal;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.goal.LookAtGoal;
import net.thenextlvl.character.goal.PatrolGoal;
import net.thenextlvl.character.goal.Target;
import net.thenextlvl.character.goal.WanderGoal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.movement.Doors;
import net.thenextlvl.character.plugin.character.movement.EntityController;
import net.thenextlvl.character.plugin.character.movement.GoalState;
import net.thenextlvl.character.plugin.model.EmptyLootTable;
import net.thenextlvl.character.plugin.property.PropertyAccessor;
import net.thenextlvl.character.plugin.property.PropertyAccessors;
import net.thenextlvl.character.property.EntityProperties;
import net.thenextlvl.character.property.EntityProperty;
import net.thenextlvl.character.property.ViewerValue;
import net.thenextlvl.character.property.ViewerValues;
import net.thenextlvl.character.tag.TagOptions;
import net.thenextlvl.nbt.NBTOutputStream;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializable;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.thenextlvl.character.plugin.CharacterPlugin.ISSUES;
import static org.bukkit.attribute.Attribute.MAX_HEALTH;
import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN;

@NullMarked
public class PaperCharacter implements Character, TagSerializable<CompoundTag> {
    private static final Pattern PROFILE_NAME_INVALID = Pattern.compile("[^a-zA-Z0-9_-]");

    protected final Class<? extends Entity> entityClass;
    protected final Map<String, ClickAction<?>> actions = Collections.synchronizedMap(new LinkedHashMap<>());
    protected final List<Goal> goals = new CopyOnWriteArrayList<>();
    // pursues the goals with the entity of every viewer
    protected final Map<UUID, EntityController> controllers = new ConcurrentHashMap<>();
    protected volatile @Nullable ScheduledTask ticker = null;
    // the state of the goals, shared by all entities if they move alike
    protected final GoalState goalState = new GoalState();
    protected volatile long ticks = 0;
    protected final Doors doors;
    protected final Set<UUID> viewers = ConcurrentHashMap.newKeySet();
    protected final Set<UUID> spawning = ConcurrentHashMap.newKeySet();
    protected final Map<String, Tag> properties = new ConcurrentHashMap<>();
    protected final Map<String, Key> viewerValues = new ConcurrentHashMap<>();
    protected final Map<UUID, Entity> entities = new ConcurrentHashMap<>();
    // the name tag of every viewer, riding the entity of that viewer
    protected final Map<UUID, TextDisplay> tags = new ConcurrentHashMap<>();
    protected final PaperTagOptions tagOptions = new PaperTagOptions();

    protected final CharacterPlugin plugin;
    protected final EntityType type;
    protected final String name;

    protected volatile @Nullable String displayName = "<character>";
    protected volatile @Nullable NamedTextColor teamColor = null;
    protected volatile @Nullable String viewPermission = null;

    protected volatile Location location;
    protected volatile Path dataFile;
    protected volatile Path backupFile;

    protected volatile boolean displayNameVisible = true;
    protected volatile boolean persistent = true;
    protected volatile boolean visibleByDefault = true;

    public PaperCharacter(final CharacterPlugin plugin, final String name, final Location location, final EntityType type) {
        final var entityClass = type.getEntityClass();
        Preconditions.checkArgument(entityClass != null && type.isSpawnable(), "Entity type %s is not spawnable", type);
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        this.entityClass = entityClass;
        this.location = location.clone();
        this.name = name;
        this.plugin = plugin;
        this.doors = new Doors(plugin, this);
        this.type = type;

        final var dataFolder = plugin.characterProvider().getDataFolder(location.getWorld());
        this.dataFile = dataFolder.resolve(name + ".dat");
        this.backupFile = dataFolder.resolve(name + ".dat_old");
    }

    @Override
    public Optional<ClickAction<?>> getAction(final String name) {
        return Optional.ofNullable(actions.get(name));
    }

    @Override
    public @Unmodifiable Map<String, ClickAction<?>> getActions() {
        return Map.copyOf(actions);
    }

    @Override
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    @Override
    public Optional<Component> getDisplayName(final Player viewer) {
        return getDisplayName().map(displayName -> parseDisplayName(displayName, viewer));
    }

    private Component parseDisplayName(final String displayName, final Player viewer) {
        final var papiFormatter = plugin.papiFormatter();
        final var formatted = papiFormatter != null ? papiFormatter.format(viewer, displayName) : displayName;

        final var builder = TagResolver.builder();
        builder.resolver(StandardTags.defaults());

        final var miniFormatter = plugin.miniFormatter();
        if (miniFormatter != null) builder.resolver(miniFormatter.tagResolver());

        builder.resolver(Placeholder.component("character", Component.text(name)));
        builder.resolver(Placeholder.parsed("player", viewer.getName()));
        builder.resolver(Placeholder.component("players", Component.text(plugin.getServer().getOnlinePlayers().size())));

        return MiniMessage.miniMessage().deserialize(formatted, viewer, builder.build());
    }

    @Override
    public @Unmodifiable List<Goal> getGoals() {
        return List.copyOf(goals);
    }

    @Override
    public boolean addGoal(final Goal goal) {
        return addGoal(goals.size(), goal);
    }

    @Override
    public synchronized boolean addGoal(final int index, final Goal goal) {
        Preconditions.checkPositionIndex(index, goals.size());
        if (goals.contains(goal)) return false;
        goals.add(index, goal);
        entities.forEach((viewer, entity) -> plugin.supply(entity, () -> {
            updateAI(entity);
            startController(viewer, entity);
        }));
        return true;
    }

    // controllers notice removed goals on their next tick
    @Override
    public boolean removeGoal(final Goal goal) {
        if (!goals.remove(goal)) return false;
        if (goals.isEmpty()) updateAI();
        return true;
    }

    @Override
    public boolean clearGoals() {
        if (goals.isEmpty()) return false;
        goals.clear();
        updateAI();
        return true;
    }

    // must be called on the region of the entity
    private void startController(final UUID viewer, final Entity entity) {
        controllers.compute(viewer, (ignored, existing) -> {
            if (existing != null && !existing.isStopped() && existing.entity().equals(entity)) return existing;
            return new EntityController(plugin, this, viewer, entity);
        });
        startTicking();
    }

    // one task per character ticks all of its entities in sync
    private synchronized void startTicking() {
        final var ticker = this.ticker;
        if (ticker != null && !ticker.isCancelled()) return;
        this.ticker = plugin.getServer().getRegionScheduler().runAtFixedRate(plugin, getLocation(), task -> tickControllers(), 1, 1);
    }

    private synchronized void stopTicking() {
        final var ticker = this.ticker;
        if (ticker != null) ticker.cancel();
        this.ticker = null;
    }

    /**
     * Ticks the controllers of all entities, on the region of the character's location.
     * <p>
     * The entities are usually close to the character's location and on the same region,
     * those that walked onto another region are ticked through their own scheduler instead.
     */
    public void tickControllers() {
        final var ticks = this.ticks;
        this.ticks = ticks + 1;
        if (controllers.isEmpty()) {
            stopTicking();
            // a controller may have been added in the meantime
            if (!controllers.isEmpty()) startTicking();
            return;
        }
        controllers.forEach((viewer, controller) -> {
            final var entity = controller.entity();
            if (plugin.getServer().isOwnedByCurrentRegion(entity)) {
                if (!controller.tick()) controllers.remove(viewer, controller);
            } else entity.getScheduler().run(plugin, task -> {
                if (!controller.tick()) controllers.remove(viewer, controller);
            }, () -> controllers.remove(viewer, controller));
        });
    }

    /**
     * Gives the entity AI if the character has goals, so it can move by velocity, but it is never aware,
     * so it does not act on its own.
     * <p>
     * An entity that is still walking back to the character's location after its goals were removed
     * keeps its AI until it arrived, see {@link #setPhysics(Entity, boolean)}.
     * <p>
     * Must be called on the region of the entity.
     *
     * @param entity the entity
     */
    public void updateAI(final Entity entity) {
        if (entity instanceof final Mob mob) mob.setAware(false);
        if (!(entity instanceof final LivingEntity living)) return;
        final var viewer = getViewer(entity);
        final var controller = viewer != null ? controllers.get(viewer) : null;
        final var moving = controller != null && controller.hasPhysics();
        living.setAI(!goals.isEmpty() || moving);
    }

    private void updateAI() {
        entities.values().forEach(entity -> plugin.supply(entity, () -> updateAI(entity)));
    }

    public Doors doors() {
        return doors;
    }

    public GoalState goalState() {
        return goalState;
    }

    public long ticks() {
        return ticks;
    }

    /**
     * Enables or disables the physics of the given entity, which moves it by velocity.
     * <p>
     * Characters have no gravity, and mobs without AI ignore velocity.
     * With physics, the entity has gravity and AI, but mobs are never aware, so they do not act on their own.
     * Entities of the same character overlap, so they do not collide while moving.
     * Disabling the physics restores the state the entity was spawned with,
     * with AI only if the character has goals, see {@link #updateAI(Entity)}.
     * <p>
     * Must be called on the region of the entity.
     *
     * @param entity  the entity
     * @param physics whether the entity should have physics
     */
    public void setPhysics(final Entity entity, final boolean physics) {
        entity.setGravity(physics);
        entity.setVelocity(new Vector());
        if (entity instanceof final LivingEntity living) {
            living.setAI(physics || !goals.isEmpty());
            living.setCollidable(!physics);
        }
        if (entity instanceof final Mob mob) mob.setAware(false);
        if (entity instanceof final Mannequin mannequin) mannequin.setImmovable(!physics);
        if (physics) return;
        Stream.of(
                EntityProperties.ENTITY_GRAVITY,
                EntityProperties.LIVING_ENTITY_COLLIDABLE,
                EntityProperties.MANNEQUIN_IMMOVABLE
        ).forEach(property -> PropertyAccessors.of(property).ifPresent(accessor -> {
            final var value = properties.get(accessor.key().asString());
            if (value != null) accessor.apply(entity, value, plugin.nbt());
        }));
    }

    /**
     * Checks if the entities of this character move alike for all viewers.
     * <p>
     * This is the case if no movement goal depends on the viewer,
     * only then may the entities change the world, like opening doors.
     *
     * @return {@code true} if the entities move alike for all viewers
     */
    public boolean movesAlike() {
        return goals.stream().allMatch(goal -> switch (goal) {
            case final FollowGoal follow ->
                    !(follow.target() instanceof Target.Viewer || follow.target() instanceof Target.NearestPlayer);
            case final WanderGoal ignored -> true;
            case final PatrolGoal ignored -> true;
            case final LookAtGoal ignored -> true;
        });
    }

    @Override
    public Optional<Location> getLocation(final Player player) {
        return getEntity(player).map(Entity::getLocation);
    }

    @Override
    public <V> Optional<V> getEntity(final Player player, final Class<V> type) {
        return getEntity(player).filter(type::isInstance).map(type::cast);
    }

    @Override
    public Optional<Entity> getEntity(final Player player) {
        return Optional.ofNullable(entities.get(player.getUniqueId()));
    }

    @Override
    public Stream<Entity> getEntities() {
        return entities.values().stream();
    }

    public @Nullable Player getOwner(final Entity entity) {
        final var viewer = getViewer(entity);
        return viewer != null ? plugin.getServer().getPlayer(viewer) : null;
    }

    private @Nullable UUID getViewer(final Entity entity) {
        return entities.entrySet().stream()
                .filter(entry -> entry.getValue().equals(entity))
                .findAny()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    @Override
    public Optional<NamedTextColor> getTeamColor() {
        return Optional.ofNullable(teamColor);
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> getViewPermission() {
        return Optional.ofNullable(viewPermission);
    }

    @Override
    public TagOptions getTagOptions() {
        return tagOptions;
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
    public World getWorld() {
        return location.getWorld();
    }

    public boolean isInChunk(final Chunk chunk) {
        if (!chunk.getWorld().equals(getWorld())) return false;
        final var chunkX = location.getBlockX() >> 4;
        final var chunkZ = location.getBlockZ() >> 4;
        return chunkX == chunk.getX() && chunkZ == chunk.getZ();
    }

    @Override
    public CompletableFuture<Boolean> teleportAsync(final Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        final var previous = this.location;
        final var event = new CharacterTeleportEvent(this, previous, location);
        if (!event.callEvent()) return CompletableFuture.completedFuture(false);
        final var destination = event.getTo();
        if (previous.equals(destination)) return CompletableFuture.completedFuture(false);

        if (!previous.getWorld().equals(destination.getWorld())) {
            if (!updatePaths(destination.getWorld())) return CompletableFuture.completedFuture(false);
            this.location = destination;
            return despawn().thenCompose(ignored -> spawn()).thenApply(ignored -> true);
        }

        this.location = destination;
        // the task ticking the entities belongs to the region of the character's location
        if (ticker != null) {
            stopTicking();
            if (!controllers.isEmpty()) startTicking();
        }
        if (!destination.isChunkLoaded()) return despawn().thenApply(ignored -> true);

        final var chunkKey = Chunk.getChunkKey(destination);
        final var teleports = new ArrayList<CompletableFuture<Boolean>>();
        entities.forEach((uuid, entity) -> {
            final var player = plugin.getServer().getPlayer(uuid);
            teleports.add(player == null ? removeEntity(uuid) : compose(player, () -> player.isChunkSent(chunkKey)
                    ? entity.teleportAsync(destination, PLUGIN)
                    : removeEntity(uuid)));
        });
        return CompletableFuture.allOf(teleports.toArray(CompletableFuture[]::new))
                .thenCompose(ignored -> spawn())
                .thenApply(ignored -> true);
    }

    private synchronized boolean updatePaths(final World world) {
        final var dataFolder = plugin.characterProvider().getDataFolder(world);
        final var dataFile = dataFolder.resolve(name + ".dat");
        final var backupFile = dataFolder.resolve(name + ".dat_old");

        try {
            Files.createDirectories(dataFolder);
            if (Files.isRegularFile(this.dataFile))
                Files.move(this.dataFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            if (Files.isRegularFile(this.backupFile))
                Files.move(this.backupFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            plugin.getComponentLogger().warn("Failed to move character data files for: {}", getName(), e);
            plugin.getComponentLogger().warn("Please look for similar issues or report this on GitHub: {}", ISSUES);
            CharacterPlugin.ERROR_TRACKER.trackError(e);
            return false;
        }

        this.dataFile = dataFile;
        this.backupFile = backupFile;
        return true;
    }

    @Override
    public boolean addAction(final String name, final ClickAction<?> action) {
        return isApplicable(action) && !action.equals(actions.put(name, action));
    }

    private <T> boolean isApplicable(final ClickAction<T> action) {
        return action.getActionType().isApplicable(action.getInput(), this);
    }

    @Override
    public void forEachAction(final BiConsumer<String, ? super ClickAction<?>> action) {
        actions.forEach(action);
    }

    @Override
    public boolean addViewer(final UUID player) {
        if (!new CharacterViewerAddEvent(this, player).callEvent()) return false;
        if (!viewers.add(player)) return false;
        if (isVisibleByDefault()) return true;
        final var online = plugin.getServer().getPlayer(player);
        if (online != null) updateVisibility(online);
        return true;
    }

    @Override
    public boolean addViewers(final Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean canSee(final Player player) {
        if (!player.getWorld().equals(getWorld())) return false;
        final var viewPermission = this.viewPermission;
        if (viewPermission != null && !player.hasPermission(viewPermission)) return false;
        return isVisibleByDefault() || isViewer(player.getUniqueId());
    }

    @Override
    public boolean hasAction(final ClickAction<?> action) {
        return actions.containsValue(action);
    }

    @Override
    public boolean hasAction(final String name) {
        return actions.containsKey(name);
    }

    @Override
    public boolean hasActions() {
        return !actions.isEmpty();
    }

    @Override
    public boolean isDisplayNameVisible() {
        return displayNameVisible;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public boolean isSpawned() {
        return entities.values().stream().anyMatch(Entity::isValid);
    }

    @Override
    public boolean isSpawned(final Player player) {
        return getEntity(player).filter(Entity::isValid).isPresent();
    }

    @Override
    public boolean isPart(final Entity entity) {
        return entities.containsValue(entity) || (entity instanceof final TextDisplay display && tags.containsValue(display));
    }

    @Override
    public Stream<Player> getTrackedBy() {
        return entities.keySet().stream()
                .map(plugin.getServer()::getPlayer)
                .filter(Objects::nonNull);
    }

    @Override
    public boolean isTrackedBy(final Player player) {
        return entities.containsKey(player.getUniqueId());
    }

    @Override
    public boolean isViewer(final UUID player) {
        return viewers.contains(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    @Override
    public synchronized boolean persist() {
        if (!isPersistent()) return false;
        final var file = getDataFile();
        final var backup = getBackupFile();
        try {
            if (Files.isRegularFile(file)) Files.move(file, backup, StandardCopyOption.REPLACE_EXISTING);
            else Files.createDirectories(file.toAbsolutePath().getParent());
            try (final var outputStream = NBTOutputStream.create(file)) {
                outputStream.writeTag(getName(), serialize());
                return true;
            }
        } catch (final Throwable t) {
            if (Files.isRegularFile(backup)) try {
                Files.copy(backup, file, StandardCopyOption.REPLACE_EXISTING);
                plugin.getComponentLogger().warn("Recovered {} from potential data loss", getName());
            } catch (final IOException e) {
                plugin.getComponentLogger().error("Failed to restore character {}", getName(), e);
            }
            plugin.getComponentLogger().error("Failed to save character {}", getName(), t);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            return false;
        }
    }

    @Override
    public boolean removeAction(final String name) {
        return actions.remove(name) != null;
    }

    @Override
    public boolean removeViewer(final UUID player) {
        if (!new CharacterViewerRemoveEvent(this, player).callEvent()) return false;
        if (!viewers.remove(player)) return false;
        if (isVisibleByDefault()) return true;
        final var online = plugin.getServer().getPlayer(player);
        if (online != null) updateVisibility(online);
        return true;
    }

    @Override
    public boolean removeViewers(final Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean setDisplayName(@Nullable final String displayName) {
        if (Objects.equals(displayName, this.displayName)) return false;
        this.displayName = displayName;
        entities.forEach(this::updateTag);
        return true;
    }

    @Override
    public boolean setDisplayNameVisible(final boolean visible) {
        if (visible == displayNameVisible) return false;
        this.displayNameVisible = visible;
        entities.forEach(this::updateTag);
        return true;
    }

    @Override
    public boolean setPersistent(final boolean persistent) {
        if (persistent == this.persistent) return false;
        this.persistent = persistent;
        return true;
    }

    @Override
    public boolean setTeamColor(@Nullable final NamedTextColor color) {
        if (color == this.teamColor) return false;
        this.teamColor = color;
        tags.forEach((viewer, tag) -> plugin.supply(tag, () -> updateTagText(viewer, tag)));
        entities.values().forEach(this::updateTeamOptions);
        return true;
    }

    @Override
    public boolean setViewPermission(@Nullable final String permission) {
        if (Objects.equals(permission, viewPermission)) return false;
        this.viewPermission = permission;
        plugin.getServer().getOnlinePlayers().forEach(this::updateVisibility);
        return true;
    }

    @Override
    public boolean setVisibleByDefault(final boolean visible) {
        if (visible == visibleByDefault) return false;
        this.visibleByDefault = visible;
        plugin.getServer().getOnlinePlayers().forEach(this::updateVisibility);
        return true;
    }

    public void loadCharacter(final Player player) {
        updateVisibility(player);
    }

    @Override
    public CompletableFuture<Boolean> spawn() {
        return anyMatch(plugin.getServer().getOnlinePlayers().stream().map(this::updateVisibility));
    }

    @Override
    public CompletableFuture<Boolean> spawn(final Player player) {
        return compose(player, () -> spawnEntity(player));
    }

    @Override
    public CompletableFuture<Boolean> despawn() {
        return anyMatch(Set.copyOf(entities.keySet()).stream().map(this::removeEntity));
    }

    @Override
    public CompletableFuture<Boolean> despawn(final Player player) {
        return removeEntity(player.getUniqueId());
    }

    // must be called on the region of the player
    private CompletableFuture<Boolean> spawnEntity(final Player player) {
        final var uuid = player.getUniqueId();
        final var location = getLocation();
        if (!player.isConnected() || !location.isChunkLoaded()) return CompletableFuture.completedFuture(false);
        if (!canSee(player)) return removeEntity(uuid).thenApply(ignored -> false);

        final var existing = entities.get(uuid);
        if (existing != null && existing.isValid()) return CompletableFuture.completedFuture(false);
        if (!spawning.add(uuid)) return CompletableFuture.completedFuture(false);

        final var resolved = resolveViewerValues(player);
        return plugin.supply(location, () -> {
            if (existing != null) invalidate(existing);
            if (!location.isChunkLoaded()) return null;

            final var entity = location.getWorld().spawn(location, entityClass, false, spawned -> preSpawn(spawned, resolved));
            if (!entity.isValid()) return null;
            entities.put(uuid, entity);

            spawnTag(uuid, entity);
            updateTeamOptions(entity);
            if (!goals.isEmpty()) startController(uuid, entity);
            return entity;
        }).thenCompose(entity -> entity != null
                ? showEntity(player, entity)
                : CompletableFuture.completedFuture(false)
        ).whenComplete((ignored, throwable) -> spawning.remove(uuid));
    }

    private CompletableFuture<Boolean> showEntity(final Player player, final Entity entity) {
        return plugin.supply(player, () -> {
            if (!player.isConnected()) return false;
            if (plugin.getServer().isOwnedByCurrentRegion(entity)) player.showEntity(plugin, entity);
            showTag(player);
            return true;
        }).thenCompose(shown -> Boolean.TRUE.equals(shown)
                ? CompletableFuture.completedFuture(true)
                : removeEntity(player.getUniqueId()).thenApply(ignored -> false));
    }

    private CompletableFuture<Boolean> removeEntity(final UUID player) {
        final var entity = entities.get(player);
        if (entity == null) return CompletableFuture.completedFuture(false);
        invalidate(entity);
        return plugin.supply(entity, entity::remove).thenApply(ignored -> true);
    }

    public void invalidate(final Entity entity) {
        if (entity instanceof final TextDisplay display && tags.values().remove(display)) return;
        final var viewer = getViewer(entity);
        if (viewer == null) return;
        entities.remove(viewer, entity);
        removeTag(viewer);
        controllers.remove(viewer);
        if (entities.isEmpty()) doors.closeAll();
        final var owner = plugin.getServer().getPlayer(viewer);
        if (owner != null && !CharacterPlugin.RUNNING_FOLIA) {
            final var team = owner.getScoreboard().getTeam(entity.getScoreboardEntryName());
            if (team != null) team.unregister();
        }
    }

    public CompletableFuture<Boolean> updateVisibility(final Player player) {
        return compose(player, () -> {
            if (!canSee(player) || player.isChunkSent(Chunk.getChunkKey(location))) return spawnEntity(player);
            return CompletableFuture.completedFuture(false);
        });
    }

    private CompletableFuture<Boolean> compose(final Entity entity, final Supplier<CompletableFuture<Boolean>> supplier) {
        return plugin.supply(entity, supplier).thenCompose(future -> future != null
                ? future
                : CompletableFuture.completedFuture(false));
    }

    private CompletableFuture<Boolean> anyMatch(final Stream<CompletableFuture<Boolean>> stream) {
        final var futures = stream.toList();
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> futures.stream().anyMatch(CompletableFuture::join));
    }

    private void editTags(final Consumer<TextDisplay> consumer) {
        tags.values().forEach(tag -> plugin.supply(tag, () -> consumer.accept(tag)));
    }

    protected void preSpawn(final Entity entity, final Map<PropertyAccessor<?, ?>, @Nullable Object> viewerValues) {
        try {
            internalPreSpawn(entity, viewerValues);
        } catch (final Exception e) {
            plugin.getComponentLogger().error("Failed to spawn character {}", getName(), e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            CharacterPlugin.ERROR_TRACKER.trackError(e);
            entity.remove();
        }
    }

    @SuppressWarnings("PatternValidation")
    protected void internalPreSpawn(final Entity entity, final Map<PropertyAccessor<?, ?>, @Nullable Object> viewerValues) {
        entity.setVisibleByDefault(false);
        entity.setGravity(false);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setPersistent(false);

        if (entity instanceof final Mannequin mannequin) {
            final var profile = ResolvableProfile.resolvableProfile();
            final var name = profileName(this.name);
            if (!name.isEmpty()) profile.name(name);
            mannequin.setProfile(profile.build());
            mannequin.setImmovable(true);
        }

        if (entity instanceof final TNTPrimed primed) primed.setFuseTicks(Integer.MAX_VALUE);

        if (entity instanceof final LivingEntity living) {
            // only entities with goals have AI, see updateAI
            living.setAI(!goals.isEmpty());
            living.setCanPickupItems(false);
            living.setRemoveWhenFarAway(false);
            final var instance = living.getAttribute(MAX_HEALTH);
            if (instance != null) living.setHealth(instance.getValue());
        }

        if (entity instanceof final Mob mob) {
            mob.setAware(false);
            mob.setLootTable(EmptyLootTable.INSTANCE);
            mob.setDespawnInPeacefulOverride(TriState.FALSE);
        }

        if (entity instanceof final Attributable attributable) {
            final var attribute = attributable.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE);
            if (attribute != null) attribute.setBaseValue(0);
        }

        PropertyAccessors.ALL.forEach(accessor -> {
            final var value = properties.get(accessor.key().asString());
            if (value != null) accessor.apply(entity, value, plugin.nbt());
        });
        viewerValues.forEach((accessor, value) -> accessor.set(entity, value));

        if (entity.getPose().equals(Pose.DYING)) {
            entity.setPose(Pose.STANDING, true);
        }
    }

    public static String profileName(final String name) {
        final var sanitized = PROFILE_NAME_INVALID.matcher(name).replaceAll("");
        return sanitized.length() > 16 ? sanitized.substring(0, 16) : sanitized;
    }

    protected Team getCharacterSettingsTeam(final Entity entity, final Player player) {
        var characterSettings = player.getScoreboard().getTeam(entity.getScoreboardEntryName());
        if (characterSettings != null) return characterSettings;
        characterSettings = player.getScoreboard().registerNewTeam(entity.getScoreboardEntryName());
        characterSettings.addEntry(entity.getScoreboardEntryName());
        return characterSettings;
    }

    // must be called on the region of the entity
    private void spawnTag(final UUID viewer, final Entity entity) {
        if (!showDisplayName()) return;
        final var tag = entity.getWorld().spawn(entity.getLocation(), TextDisplay.class, false, display -> configureTag(viewer, display));
        if (!tag.isValid()) return;
        entity.addPassenger(tag);
        final var previous = tags.put(viewer, tag);
        if (previous != null) plugin.supply(previous, previous::remove);
    }

    // must be called on the region of the viewer
    private void showTag(final Player viewer) {
        final var tag = tags.get(viewer.getUniqueId());
        if (tag != null && plugin.getServer().isOwnedByCurrentRegion(tag)) viewer.showEntity(plugin, tag);
    }

    // spawns, updates or removes the tag of the given viewer
    private void updateTag(final UUID viewer, final Entity entity) {
        plugin.supply(entity, () -> {
            final var tag = tags.get(viewer);
            if (!showDisplayName()) {
                removeTag(viewer);
            } else if (tag != null && tag.isValid()) {
                configureTag(viewer, tag);
            } else {
                spawnTag(viewer, entity);
                final var player = plugin.getServer().getPlayer(viewer);
                if (player != null) plugin.supply(player, () -> showTag(player));
            }
        });
    }

    private void removeTag(final UUID viewer) {
        final var tag = tags.remove(viewer);
        if (tag != null) plugin.supply(tag, tag::remove);
    }

    private void configureTag(final UUID viewer, final TextDisplay display) {
        display.setAlignment(tagOptions.getAlignment());
        display.setBackgroundColor(tagOptions.getBackgroundColor().orElse(null));
        display.setBillboard(tagOptions.getBillboard());
        display.setBrightness(tagOptions.getBrightness().orElse(null));
        display.setDefaultBackground(tagOptions.isDefaultBackground());
        display.setLineWidth(tagOptions.getLineWidth());
        display.setGravity(false);
        display.setPersistent(false);
        display.setSeeThrough(tagOptions.isSeeThrough());
        display.setShadowed(tagOptions.isShadowed());
        display.setTransformation(new Transformation(
                tagOptions.getOffset(),
                tagOptions.getLeftRotation(),
                tagOptions.getScale(),
                tagOptions.getRightRotation()
        ));
        display.setVisibleByDefault(false);
        updateTagOpacity(display);
        updateTagText(viewer, display);
    }

    private void updateTagText(final UUID viewer, final TextDisplay display) {
        final var player = plugin.getServer().getPlayer(viewer);
        if (player == null) return;
        final var component = getDisplayName(player).orElseGet(() -> Component.text(name));
        display.text(component.colorIfAbsent(teamColor));
    }

    private void updateTagOpacity(final TextDisplay display) {
        display.setTextOpacity((byte) Math.round(25f + ((100f - tagOptions.getTextOpacity()) * 2.3f)));
    }

    protected boolean showDisplayName() {
        return displayName != null && displayNameVisible;
    }

    public void updateTeamOptions(final Entity entity) {
        if (CharacterPlugin.RUNNING_FOLIA) return;
        final var owner = getOwner(entity);
        if (owner != null) updateTeamOptions(getCharacterSettingsTeam(entity, owner), entity);
    }

    protected void updateTeamOptions(final Team team, final Entity entity) {
        team.color(teamColor);
        final var collidable = entity instanceof final LivingEntity living && living.isCollidable();
        team.setOption(Team.Option.COLLISION_RULE, collidable ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    @Override
    public Path getBackupFile() {
        return backupFile;
    }

    @Override
    public Path getDataFile() {
        return dataFile;
    }

    @Override
    public <T> Optional<T> getProperty(final EntityProperty<?, T> property) {
        return PropertyAccessors.of(property).flatMap(this::getProperty);
    }

    private <T> Optional<T> getProperty(final PropertyAccessor<?, T> accessor) {
        return Optional.ofNullable(properties.get(accessor.key().asString()))
                .map(value -> accessor.deserialize(value, plugin.nbt()));
    }

    @Override
    public <T> boolean setProperty(final EntityProperty<?, T> property, final T value) {
        if (!property.isApplicable(entityClass)) return false;
        return PropertyAccessors.of(property).map(accessor -> {
            final var stored = properties.get(accessor.key().asString());
            if (stored != null && Objects.equals(accessor.deserialize(stored, plugin.nbt()), value)) return false;
            final var tag = accessor.serialize(value, plugin.nbt());
            properties.put(accessor.key().asString(), tag);
            if (getViewerValue(property).isPresent()) return true;
            entities.values().forEach(entity -> plugin.supply(entity, () -> accessor.apply(entity, tag, plugin.nbt())));
            return true;
        }).orElse(false);
    }

    @Override
    public <T> Optional<T> getProperty(final EntityProperty<?, T> property, final Player viewer) {
        return getViewerValue(property).map(value -> value.resolve(viewer)).or(() -> getProperty(property));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<ViewerValue<T>> getViewerValue(final EntityProperty<?, T> property) {
        return Optional.ofNullable(viewerValues.get(property.key().asString()))
                .flatMap(ViewerValues::getByKey)
                .filter(value -> value.isApplicable(property))
                .map(value -> (ViewerValue<T>) value);
    }

    @Override
    public <T> boolean setViewerValue(final EntityProperty<?, T> property, @Nullable final ViewerValue<T> value) {
        if (!property.isApplicable(entityClass)) return false;
        if (value != null && !value.isApplicable(property)) return false;
        final var accessor = PropertyAccessors.of(property).orElse(null);
        if (accessor == null) return false;

        final var key = property.key().asString();
        final var previous = value != null ? viewerValues.put(key, value.key()) : viewerValues.remove(key);
        if (Objects.equals(previous, value != null ? value.key() : null)) return false;

        entities.forEach((uuid, entity) -> {
            final var viewer = plugin.getServer().getPlayer(uuid);
            if (viewer == null) return;
            if (value != null) plugin.supply(viewer, () -> value.resolve(viewer))
                    .thenAccept(resolved -> plugin.supply(entity, () -> accessor.set(entity, resolved)));
            else restoreProperty(viewer, entity, accessor);
        });
        return true;
    }

    // restores the persisted value, or the default value by respawning the entity if there is none
    private void restoreProperty(final Player viewer, final Entity entity, final PropertyAccessor<?, ?> accessor) {
        final var stored = properties.get(accessor.key().asString());
        if (stored != null) plugin.supply(entity, () -> accessor.apply(entity, stored, plugin.nbt()));
        else despawn(viewer).thenCompose(ignored -> spawn(viewer));
    }

    // must be called on the region of the viewer
    private Map<PropertyAccessor<?, ?>, @Nullable Object> resolveViewerValues(final Player viewer) {
        final var resolved = new LinkedHashMap<PropertyAccessor<?, ?>, @Nullable Object>();
        if (viewerValues.isEmpty()) return resolved;
        PropertyAccessors.ALL.forEach(accessor -> getViewerValue(accessor.property())
                .ifPresent(value -> resolved.put(accessor, value.resolve(viewer))));
        return resolved;
    }

    @Override
    public CompoundTag serialize() throws ParserException {
        final var tag = CompoundTag.builder();
        getDisplayName().ifPresent(displayName -> tag.put("displayName", displayName));
        tag.put("position", CompoundTag.builder()
                .put("x", location.getX())
                .put("y", location.getY())
                .put("z", location.getZ())
                .build());
        tag.put("rotation", CompoundTag.builder()
                .put("yaw", location.getYaw())
                .put("pitch", location.getPitch())
                .build());
        getTeamColor().ifPresent(teamColor -> tag.put("teamColor", plugin.nbt().serialize(teamColor)));
        getViewPermission().ifPresent(viewPermission -> tag.put("viewPermission", viewPermission));
        tag.put("displayNameVisible", isDisplayNameVisible());
        tag.put("tagOptions", tagOptions.serialize());
        tag.put("type", plugin.nbt().serialize(getType()));
        tag.put("visibleByDefault", isVisibleByDefault());
        final var actions = CompoundTag.builder();
        getActions().forEach((name, clickAction) -> actions.put(name, plugin.nbt().serialize(clickAction)));
        if (!properties.isEmpty()) {
            final var data = CompoundTag.builder();
            properties.forEach(data::put);
            tag.put("entityData", data.build());
        }
        tag.put("clickActions", actions.build());
        if (!goals.isEmpty()) {
            final var goals = ListTag.of(this.goals.stream().map(goal -> plugin.nbt().serialize(goal, Goal.class)).toList());
            tag.put("goals", goals);
        }
        if (!viewerValues.isEmpty()) {
            final var values = CompoundTag.builder();
            viewerValues.forEach((property, value) -> values.put(property, value.asString()));
            tag.put("viewerValues", values.build());
        }
        return tag.build();
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public void deserialize(final CompoundTag tag) throws ParserException {
        tag.optional("entityData").map(Tag::getAsCompound).ifPresent(entityData -> entityData.forEach(properties::put));
        tag.optional("viewerValues").map(Tag::getAsCompound).ifPresent(values -> values.forEach((property, value) ->
                viewerValues.put(property, Key.key(value.getAsString()))));
        tag.optional("goals").map(Tag::getAsList).ifPresent(goals -> goals.forEach(goal -> {
            try {
                addGoal(plugin.nbt().deserialize(goal, Goal.class));
            } catch (final ParserException e) {
                plugin.getComponentLogger().warn("Failed to load a goal of character {}: {}", getName(), e.getMessage());
            }
        }));
        tag.optional("clickActions").map(Tag::getAsCompound).ifPresent(actions -> actions.forEach((name, action) ->
                addAction(name, plugin.nbt().deserialize(action, ClickAction.class))));
        tag.optional("displayName").map(Tag::getAsString)
                .ifPresentOrElse(this::setDisplayName, () -> this.setDisplayName(null));
        tag.optional("displayNameVisible").map(Tag::getAsBoolean).ifPresent(this::setDisplayNameVisible);
        tag.optional("tagOptions").map(Tag::getAsCompound).ifPresent(tagOptions::deserialize);
        tag.optional("teamColor").map(t -> plugin.nbt().deserialize(t, NamedTextColor.class)).ifPresent(this::setTeamColor);
        tag.optional("viewPermission").map(Tag::getAsString).ifPresent(permission -> this.viewPermission = permission);
        tag.optional("visibleByDefault").map(Tag::getAsBoolean).ifPresent(visible -> this.visibleByDefault = visible);
        // the world is implied by the data folder, legacy files stored the location with its world
        final var position = tag.optional("position").or(() -> tag.optional("location")).map(Tag::getAsCompound);
        final var rotation = tag.optional("rotation").or(() -> tag.optional("location")).map(Tag::getAsCompound);
        position.ifPresent(compound -> {
            location.setX(compound.get("x").getAsDouble());
            location.setY(compound.get("y").getAsDouble());
            location.setZ(compound.get("z").getAsDouble());
        });
        rotation.ifPresent(compound -> {
            compound.optional("yaw").map(Tag::getAsFloat).ifPresent(location::setYaw);
            compound.optional("pitch").map(Tag::getAsFloat).ifPresent(location::setPitch);
        });
    }

    protected class PaperTagOptions implements TagOptions {
        private @Nullable Brightness brightness = null;
        private @Nullable Color backgroundColor = null;
        private Billboard billboard = Billboard.CENTER;
        private Quaternionf leftRotation = new Quaternionf();
        private Quaternionf rightRotation = new Quaternionf();
        private TextAlignment alignment = TextAlignment.CENTER;
        private Vector3f offset = new Vector3f(0, 0.27f, 0);
        private Vector3f scale = new Vector3f(1);
        private boolean defaultBackground = false;
        private boolean seeThrough = false;
        private boolean textShadow = false;
        private float textOpacity;
        private int lineWidth = 200;

        @Override
        public Billboard getBillboard() {
            return billboard;
        }

        @Override
        public Optional<Brightness> getBrightness() {
            return Optional.ofNullable(brightness);
        }

        @Override
        public Optional<Color> getBackgroundColor() {
            return Optional.ofNullable(backgroundColor);
        }

        @Override
        public Quaternionf getLeftRotation() {
            return leftRotation;
        }

        @Override
        public Quaternionf getRightRotation() {
            return rightRotation;
        }

        @Override
        public TextAlignment getAlignment() {
            return alignment;
        }

        @Override
        public Vector3f getOffset() {
            return offset;
        }

        @Override
        public Vector3f getScale() {
            return scale;
        }

        @Override
        public boolean isShadowed() {
            return textShadow;
        }

        @Override
        public boolean isDefaultBackground() {
            return defaultBackground;
        }

        @Override
        public boolean isSeeThrough() {
            return seeThrough;
        }

        @Override
        public boolean setAlignment(final TextAlignment alignment) {
            if (Objects.equals(alignment, this.alignment)) return false;
            editTags(display -> display.setAlignment(alignment));
            this.alignment = alignment;
            return true;
        }

        @Override
        public boolean setBackgroundColor(@Nullable final Color color) {
            if (Objects.equals(color, this.backgroundColor)) return false;
            editTags(display -> display.setBackgroundColor(color));
            this.backgroundColor = color;
            return true;
        }

        @Override
        public boolean setBillboard(final Billboard billboard) {
            if (Objects.equals(billboard, this.billboard)) return false;
            editTags(display -> display.setBillboard(billboard));
            this.billboard = billboard;
            return true;
        }

        @Override
        public boolean setBrightness(@Nullable final Brightness brightness) {
            if (Objects.equals(brightness, this.brightness)) return false;
            editTags(display -> display.setBrightness(brightness));
            this.brightness = brightness;
            return true;
        }

        @Override
        public boolean setDefaultBackground(final boolean enabled) {
            if (enabled == defaultBackground) return false;
            editTags(display -> display.setDefaultBackground(enabled));
            this.defaultBackground = enabled;
            return true;
        }

        @Override
        public boolean setLeftRotation(final Quaternionf rotation) {
            if (Objects.equals(rotation, this.leftRotation)) return false;
            this.leftRotation = rotation;
            editTags(display -> display.setTransformation(getTransformation()));
            return true;
        }

        @Override
        public boolean setLineWidth(final int width) {
            if (width == lineWidth) return false;
            editTags(display -> display.setLineWidth(width));
            this.lineWidth = width;
            return true;
        }

        @Override
        public boolean setOffset(final Vector3f offset) {
            if (Objects.equals(offset, this.offset)) return false;
            this.offset = offset;
            editTags(display -> display.setTransformation(getTransformation()));
            return true;
        }

        @Override
        public boolean setOffsetX(final float offset) {
            return setOffset(new Vector3f(offset, this.offset.y(), this.offset.z()));
        }

        @Override
        public boolean setOffsetY(final float offset) {
            return setOffset(new Vector3f(this.offset.x(), offset, this.offset.z()));
        }

        @Override
        public boolean setOffsetZ(final float offset) {
            return setOffset(new Vector3f(this.offset.x(), this.offset.y(), offset));
        }

        @Override
        public boolean setRightRotation(final Quaternionf rotation) {
            if (Objects.equals(rotation, this.rightRotation)) return false;
            this.rightRotation = rotation;
            editTags(display -> display.setTransformation(getTransformation()));
            return true;
        }

        @Override
        public boolean setScale(final Vector3f scale) {
            if (Objects.equals(scale, this.scale)) return false;
            this.scale = scale;
            editTags(display -> display.setTransformation(getTransformation()));
            return true;
        }

        private Transformation getTransformation() {
            return new Transformation(offset, leftRotation, scale, rightRotation);
        }

        @Override
        public boolean setScale(final float scale) {
            return setScale(new Vector3f(scale));
        }

        @Override
        public boolean setSeeThrough(final boolean seeThrough) {
            if (seeThrough == this.seeThrough) return false;
            editTags(display -> display.setSeeThrough(seeThrough));
            this.seeThrough = seeThrough;
            return true;
        }

        @Override
        public boolean setTextOpacity(final float opacity) {
            if (opacity == textOpacity) return false;
            this.textOpacity = opacity;
            editTags(PaperCharacter.this::updateTagOpacity);
            return true;
        }

        @Override
        public boolean setShadowed(final boolean enabled) {
            if (enabled == textShadow) return false;
            editTags(display -> display.setShadowed(enabled));
            this.textShadow = enabled;
            return true;
        }

        @Override
        public float getTextOpacity() {
            return textOpacity;
        }

        @Override
        public int getLineWidth() {
            return lineWidth;
        }

        private CompoundTag serialize() throws ParserException {
            final var tag = CompoundTag.builder();
            if (backgroundColor != null) tag.put("backgroundColor", backgroundColor.asARGB());
            if (brightness != null) tag.put("brightness", plugin.nbt().serialize(brightness));
            tag.put("alignment", alignment.name());
            tag.put("billboard", billboard.name());
            tag.put("defaultBackground", defaultBackground);
            tag.put("leftRotation", plugin.nbt().serialize(leftRotation));
            tag.put("lineWidth", lineWidth);
            tag.put("offset", plugin.nbt().serialize(offset));
            tag.put("rightRotation", plugin.nbt().serialize(rightRotation));
            tag.put("scale", plugin.nbt().serialize(scale));
            tag.put("seeThrough", seeThrough);
            tag.put("textOpacity", textOpacity);
            tag.put("textShadow", textShadow);
            return tag.build();
        }

        private void deserialize(final CompoundTag tag) throws ParserException {
            tag.optional("alignment").map(Tag::getAsString).map(TextAlignment::valueOf).ifPresent(this::setAlignment);
            tag.optional("billboard").map(Tag::getAsString).map(Billboard::valueOf).ifPresent(this::setBillboard);
            tag.optional("defaultBackground").map(Tag::getAsBoolean).ifPresent(this::setDefaultBackground);
            tag.optional("leftRotation").map(t -> plugin.nbt().deserialize(t, Quaternionf.class)).ifPresent(this::setLeftRotation);
            tag.optional("lineWidth").map(Tag::getAsInt).ifPresent(this::setLineWidth);
            tag.optional("offset").map(t -> plugin.nbt().deserialize(t, Vector3f.class)).ifPresent(this::setOffset);
            tag.optional("rightRotation").map(t -> plugin.nbt().deserialize(t, Quaternionf.class)).ifPresent(this::setRightRotation);
            tag.optional("scale").map(t -> plugin.nbt().deserialize(t, Vector3f.class)).ifPresent(this::setScale);
            tag.optional("seeThrough").map(Tag::getAsBoolean).ifPresent(this::setSeeThrough);
            tag.optional("textOpacity").map(Tag::getAsFloat).ifPresent(this::setTextOpacity);
            tag.optional("textShadow").map(Tag::getAsBoolean).ifPresent(this::setShadowed);
            setBackgroundColor(tag.optional("backgroundColor").map(Tag::getAsInt).map(Color::fromARGB).orElse(null));
            setBrightness(tag.optional("brightness").map(t -> plugin.nbt().deserialize(t, Brightness.class)).orElse(null));
        }
    }
}
