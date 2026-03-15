package net.thenextlvl.character.plugin.character;

import com.destroystokyo.paper.entity.Pathfinder;
import com.google.common.base.Preconditions;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.codec.EntityCodec;
import net.thenextlvl.character.codec.EntityCodecRegistry;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.model.EmptyLootTable;
import net.thenextlvl.character.tag.TagOptions;
import net.thenextlvl.nbt.NBTOutputStream;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagSerializable;
import net.thenextlvl.nbt.tag.ByteTag;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
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
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.thenextlvl.character.plugin.CharacterPlugin.ISSUES;
import static org.bukkit.attribute.Attribute.MAX_HEALTH;

@NullMarked
public class PaperCharacter<E extends Entity> implements Character<E>, TagSerializable<CompoundTag> {
    protected final Class<? extends E> entityClass;
    protected final Map<String, ClickAction<?>> actions = new LinkedHashMap<>();
    protected final Set<Goal> goals = new HashSet<>();
    protected final Set<UUID> viewers = new HashSet<>();
    protected final TagOptions tagOptions = new PaperTagOptions();

    protected final CharacterPlugin plugin;
    protected final EntityType type;
    protected final String name;

    protected @Nullable E entity = null;
    protected @Nullable Component displayName;
    protected @Nullable Location spawnLocation = null;
    protected @Nullable NamedTextColor teamColor = null;
    protected @Nullable String viewPermission = null;
    protected @Nullable TextDisplay textDisplayName = null;

    public @Nullable CompoundTag entityData = null;

    protected Pose pose = Pose.STANDING;

    protected boolean displayNameVisible = true;
    protected boolean persistent = true;
    protected boolean visibleByDefault = true;

    @SuppressWarnings("unchecked")
    public PaperCharacter(final CharacterPlugin plugin, final String name, final EntityType type) {
        final var entityClass = type.getEntityClass();
        Preconditions.checkArgument(entityClass != null, "Cannot spawn entity of type %s", type);
        this.entityClass = (Class<? extends E>) entityClass;
        this.displayName = Component.text(name);
        this.name = name;
        this.plugin = plugin;
        this.type = type;
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
    public Optional<Component> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    @Override
    public @Unmodifiable Set<Goal> getGoals() {
        return Set.copyOf(goals);
    }

    @Override
    public boolean addGoal(final Goal goal) {
        return goals.add(goal);
    }

    @Override
    public boolean removeGoal(final Goal goal) {
        return goals.remove(goal);
    }

    @Override
    public <V> Optional<V> getEntity(final Class<V> type) {
        return getEntity().filter(type::isInstance).map(type::cast);
    }

    @Override
    public Optional<E> getEntity() {
        return Optional.ofNullable(entity);
    }

    @Override
    public Class<? extends E> getEntityClass() {
        return entityClass;
    }

    @Override
    public Optional<NamedTextColor> getTeamColor() {
        return Optional.ofNullable(teamColor);
    }

    @Override
    public Optional<Location> getLocation() {
        return getEntity().map(Entity::getLocation);
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
    public Optional<Location> getSpawnLocation() {
        return Optional.ofNullable(spawnLocation);
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
    public Optional<World> getWorld() {
        return getEntity().map(Entity::getWorld);
    }

    @Override
    public Optional<Pathfinder> getPathfinder() {
        return getEntity(Mob.class).map(Mob::getPathfinder);
    }

    @Override
    public <T> boolean addAction(final String name, final ClickAction<T> action) {
        return action.getActionType().isApplicable(action.getInput(), this)
                && !action.equals(actions.put(name, action));
    }

    @Override
    public boolean addViewer(final UUID player) {
        if (!viewers.add(player)) return false;
        if (entity == null || isVisibleByDefault()) return true;
        final var online = plugin.getServer().getPlayer(player);
        if (online != null) online.showEntity(plugin, entity);
        return true;
    }

    @Override
    public boolean addViewers(final Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean canSee(final Player player) {
        if (entity == null || !isSpawned()) return false;
        if (!player.getWorld().equals(entity.getWorld())) return false;
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
    public boolean isDisplayNameVisible() {
        return displayNameVisible;
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
    public boolean isTrackedBy(final Player player) {
        return getEntity().map(entity -> entity.getTrackedBy().contains(player)).orElse(false);
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
    public boolean persist() {
        if (!isPersistent()) return false;
        final var file = file();
        final var backup = backupFile();
        try {
            if (Files.isRegularFile(file)) Files.move(file, backup, StandardCopyOption.REPLACE_EXISTING);
            else Files.createDirectories(file.toAbsolutePath().getParent());
            try (final var outputStream = NBTOutputStream.create(file)) {
                outputStream.writeTag(getName(), plugin.nbt().serialize(this));
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
        if (!viewers.remove(player)) return false;
        if (entity == null || isVisibleByDefault()) return true;
        final var online = plugin.getServer().getPlayer(player);
        if (online != null) online.hideEntity(plugin, entity);
        return true;
    }

    @Override
    public boolean removeViewers(final Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean setDisplayName(@Nullable final Component displayName) {
        if (Objects.equals(displayName, this.displayName)) return false;
        this.displayName = displayName;
        getEntity().ifPresent(this::updateTextDisplayName);
        return true;
    }

    @Override
    public boolean setDisplayNameVisible(final boolean visible) {
        if (visible == displayNameVisible) return false;
        this.displayNameVisible = visible;
        getEntity().ifPresent(this::updateTextDisplayName);
        return true;
    }

    @Override
    public boolean setPersistent(final boolean persistent) {
        if (persistent == this.persistent) return false;
        this.persistent = persistent;
        return true;
    }

    @Override
    public boolean setSpawnLocation(@Nullable final Location location) {
        if (Objects.equals(location, spawnLocation)) return false;
        this.spawnLocation = location;
        return true;
    }

    @Override
    public boolean setTeamColor(@Nullable final NamedTextColor color) {
        if (color == this.teamColor) return false;
        this.teamColor = color;
        textDisplayName().ifPresent(this::updateTextDisplayNameText);
        getEntity().ifPresent(this::updateTeamOptions);
        return true;
    }

    @Override
    public boolean setViewPermission(@Nullable final String permission) {
        if (Objects.equals(permission, viewPermission)) return false;
        this.viewPermission = permission;
        getEntity().ifPresent(entity -> plugin.getServer().getOnlinePlayers()
                .forEach(player -> updateVisibility(entity, player)));
        return true;
    }

    @Override
    public boolean setVisibleByDefault(final boolean visible) {
        if (visible == visibleByDefault) return false;
        this.visibleByDefault = visible;
        getEntity().ifPresent(entity -> {
            entity.setVisibleByDefault(visible);
            if (textDisplayName != null) textDisplayName.setVisibleByDefault(visible);
            if (visible) entity.getTrackedBy().forEach(player -> {
                if (isViewer(player.getUniqueId())) return;
                if (textDisplayName != null) player.hideEntity(plugin, textDisplayName);
                player.hideEntity(plugin, entity);
            });
            else getViewers().stream().map(plugin.getServer()::getPlayer)
                    .filter(Objects::nonNull).forEach(player -> {
                        if (textDisplayName != null) player.showEntity(plugin, textDisplayName);
                        player.showEntity(plugin, entity);
                    });
        });
        return true;
    }

    public void loadCharacter(final Player player) {
        getEntity().ifPresent(entity -> {
            updateTeamOptions(getCharacterSettingsTeam(entity, player));
            updateVisibility(entity, player);
        });
    }

    @Override
    public @Nullable E spawn() throws IllegalStateException {
        if (spawnLocation == null) return null;
        return spawn(spawnLocation);
    }

    @Override
    public E spawn(final Location location) throws IllegalStateException {
        Preconditions.checkState(!isSpawned(), "Character '%s' is already spawned", name);
        Preconditions.checkState(location.isChunkLoaded(), "Chunk at %s, %s in %s is not loaded",
                location.getBlockX() >> 4, location.getBlockZ() >> 4, location.getWorld().key());

        this.spawnLocation = location;
        this.entity = location.getWorld().spawn(location, entityClass, this::preSpawn);

        if (viewPermission != null || !visibleByDefault) plugin.getServer().getOnlinePlayers()
                .forEach(player -> updateVisibility(entity, player));

        updateTextDisplayName(entity);
        updateTeamOptions(entity);
        return entity;
    }

    @Override
    public @Nullable E respawn() throws IllegalStateException {
        if (spawnLocation == null) return null;
        return respawn(spawnLocation);
    }

    @Override
    public E respawn(final Location location) throws IllegalStateException {
        remove();
        return spawn(location);
    }

    @Override
    public void delete() {
        remove();
        try {
            Files.deleteIfExists(backupFile());
            Files.deleteIfExists(file());
        } catch (final IOException e) {
            plugin.getComponentLogger().error("Failed to delete character {}", getName(), e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            CharacterPlugin.ERROR_TRACKER.trackError(e);
        }
        plugin.characterController().unregister(name);
    }

    @Override
    public void remove() {
        if (entity != null) entity.remove();
        invalidate();
    }

    public void invalidate() {
        removeTextDisplayName();
        if (entity != null) plugin.getServer().getOnlinePlayers().forEach(player -> {
            final var team = player.getScoreboard().getTeam(entity.getScoreboardEntryName());
            if (team != null) team.unregister();
        });
        entity = null;
    }

    public void updateVisibility(final E entity, final Player player) {
        if (canSee(player)) {
            if (textDisplayName != null) player.showEntity(plugin, textDisplayName);
            player.showEntity(plugin, entity);
        } else {
            if (textDisplayName != null) player.hideEntity(plugin, textDisplayName);
            player.hideEntity(plugin, entity);
        }
    }

    private Optional<TextDisplay> textDisplayName() {
        return Optional.ofNullable(textDisplayName);
    }

    protected void preSpawn(final E entity) {
        try {
            internalPreSpawn(entity);
        } catch (final Exception e) {
            plugin.getComponentLogger().error("Failed to spawn character {}", getName(), e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            CharacterPlugin.ERROR_TRACKER.trackError(e);
            entity.remove();
        }
    }

    @SuppressWarnings("PatternValidation")
    protected void internalPreSpawn(final E entity) {
        entity.setVisibleByDefault(visibleByDefault);
        entity.setGravity(false);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setPersistent(false);

        if (entity instanceof final Mannequin mannequin) {
            final var name = this.name.length() > 16 ? this.name.substring(0, 16) : this.name;
            mannequin.setProfile(ResolvableProfile.resolvableProfile().name(name).build());
            mannequin.setImmovable(true);
        }

        if (entity instanceof final TNTPrimed primed) primed.setFuseTicks(Integer.MAX_VALUE);

        if (entity instanceof final LivingEntity living) {
            living.setAI(false);
            living.setCanPickupItems(false);
            living.setRemoveWhenFarAway(false);
            final var instance = living.getAttribute(MAX_HEALTH);
            if (instance != null) living.setHealth(instance.getValue());
        }

        if (entity instanceof final Mob mob) {
            mob.setLootTable(EmptyLootTable.INSTANCE);
            mob.setDespawnInPeacefulOverride(TriState.FALSE);
        }

        if (entity instanceof final Attributable attributable) {
            final var attribute = attributable.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE);
            if (attribute != null) attribute.setBaseValue(0);
        }

        if (entityData != null) EntityCodecRegistry.registry().codecs().forEach(entityCodec -> {
            if (!entityCodec.entityType().isInstance(entity)) return;
            @SuppressWarnings("unchecked") final var codec = (EntityCodec<Object, Object>) entityCodec;
            entityData.optional(entityCodec.key().asString())
                    .map(tag1 -> tag1 instanceof final ByteTag byteTag && byteTag.getAsByte() == -1
                            ? null : codec.adapter().deserialize(tag1, plugin.nbt()))
                    .ifPresent(data -> codec.setter().test(entity, data));
        });
        entityData = null;

        if (entity.getPose().equals(Pose.DYING)) {
            entity.setPose(Pose.STANDING, true);
        }
    }

    protected Team getCharacterSettingsTeam(final Entity entity, final Player player) {
        var characterSettings = player.getScoreboard().getTeam(entity.getScoreboardEntryName());
        if (characterSettings != null) return characterSettings;
        characterSettings = player.getScoreboard().registerNewTeam(entity.getScoreboardEntryName());
        characterSettings.addEntry(entity.getScoreboardEntryName());
        return characterSettings;
    }

    protected void removeTextDisplayName() {
        if (textDisplayName == null) return;
        textDisplayName.remove();
        textDisplayName = null;
    }

    private void updateTextDisplayName(final TextDisplay display) {
        display.setAlignment(tagOptions.getAlignment());
        display.setBackgroundColor(tagOptions.getBackgroundColor());
        display.setBillboard(tagOptions.getBillboard());
        display.setBrightness(tagOptions.getBrightness());
        display.setDefaultBackground(tagOptions.isDefaultBackground());
        display.setLineWidth(tagOptions.getLineWidth());
        display.setGravity(false);
        display.setPersistent(false);
        display.setSeeThrough(tagOptions.isSeeThrough());
        display.setShadowed(tagOptions.hasTextShadow());
        display.setTransformation(new Transformation(
                tagOptions.getOffset(),
                tagOptions.getLeftRotation(),
                tagOptions.getScale(),
                tagOptions.getRightRotation()
        ));
        display.setVisibleByDefault(visibleByDefault);
        updateTextDisplayNameOpacity(display);
        updateTextDisplayNameText(display);
    }

    private void updateTextDisplayNameText(final TextDisplay display) {
        final var component = displayName == null ? Component.text(getName()) : displayName;
        display.text(component.colorIfAbsent(teamColor));
    }

    private void updateTextDisplayNameOpacity(final TextDisplay display) {
        display.setTextOpacity((byte) Math.round(25f + ((100f - tagOptions.getTextOpacity()) * 2.3f)));
    }

    protected void updateTextDisplayName(final E entity) {
        if (textDisplayName == null && showDisplayName()) {
            textDisplayName = entity.getWorld().spawn(entity.getLocation(), TextDisplay.class, display -> {
                entity.addPassenger(display);
                updateTextDisplayName(display);
            });
        } else if (textDisplayName != null && !showDisplayName()) {
            removeTextDisplayName();
        } else if (textDisplayName != null) {
            updateTextDisplayName(textDisplayName);
        }
    }

    protected boolean showDisplayName() {
        return displayName != null && displayNameVisible;
    }

    public void updateTeamOptions(final E entity) {
        entity.getTrackedBy().forEach(player -> updateTeamOptions(getCharacterSettingsTeam(entity, player)));
    }

    protected void updateTeamOptions(final Team team) {
        team.color(teamColor);
        final var collidable = getEntity(LivingEntity.class).map(LivingEntity::isCollidable).orElse(false);
        team.setOption(Team.Option.COLLISION_RULE, collidable ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    private Path backupFile() {
        return plugin.savesFolder().resolve(this.name + ".dat_old");
    }

    private Path file() {
        return plugin.savesFolder().resolve(this.name + ".dat");
    }

    @Override
    public CompoundTag serialize() throws ParserException {
        final var tag = CompoundTag.builder();
        getDisplayName().ifPresent(displayName -> tag.put("displayName", plugin.nbt().serialize(displayName)));
        getSpawnLocation().ifPresent(spawnLocation -> tag.put("location", plugin.nbt().serialize(spawnLocation)));
        getTeamColor().ifPresent(teamColor -> tag.put("teamColor", plugin.nbt().serialize(teamColor)));
        getViewPermission().ifPresent(viewPermission -> tag.put("viewPermission", viewPermission));
        tag.put("displayNameVisible", isDisplayNameVisible());
        tag.put("tagOptions", getTagOptions().serialize());
        tag.put("type", plugin.nbt().serialize(getType()));
        tag.put("visibleByDefault", isVisibleByDefault());
        final var actions = CompoundTag.builder();
        getActions().forEach((name, clickAction) -> actions.put(name, plugin.nbt().serialize(clickAction)));
        final var data = getEntity().map(entity -> {
            final var entityData = CompoundTag.builder();
            EntityCodecRegistry.registry().codecs().forEach(entityCodec -> {
                if (!entityCodec.entityType().isInstance(entity)) return;
                @SuppressWarnings("unchecked") final var codec = (EntityCodec<Object, Object>) entityCodec;
                final var object = codec.getter().apply(entity);
                if (object == null) entityData.put(codec.key().asString(), ByteTag.of((byte) -1));
                else entityData.put(codec.key().asString(), codec.adapter().serialize(object, plugin.nbt()));
            });
            return this.entityData = entityData.build();
        }).orElseGet(() -> this.entityData);
        if (data != null) tag.put("entityData", data);
        tag.put("clickActions", actions.build());
        return tag.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserialize(final CompoundTag tag) throws ParserException {
        tag.optional("entityData").map(Tag::getAsCompound).ifPresent(entityData -> this.entityData = entityData);
        tag.optional("clickActions").map(Tag::getAsCompound).ifPresent(actions -> actions.forEach((name, action) ->
                addAction(name, (ClickAction<@NonNull Object>) plugin.nbt().deserialize(action, ClickAction.class))));
        tag.optional("displayName").map(t -> plugin.nbt().deserialize(t, Component.class))
                .ifPresentOrElse(this::setDisplayName, () -> this.setDisplayName(null));
        tag.optional("displayNameVisible").map(Tag::getAsBoolean).ifPresent(this::setDisplayNameVisible);
        tag.optional("tagOptions").map(Tag::getAsCompound).ifPresent(tagOptions::deserialize);
        tag.optional("teamColor").map(t -> plugin.nbt().deserialize(t, NamedTextColor.class)).ifPresent(this::setTeamColor);
        tag.optional("viewPermission").map(Tag::getAsString).ifPresent(this::setViewPermission);
        tag.optional("visibleByDefault").map(Tag::getAsBoolean).ifPresent(this::setVisibleByDefault);
        try {
            tag.optional("location")
                    .map(location -> plugin.nbt().deserialize(location, Location.class))
                    .ifPresent(this::setSpawnLocation);
        } catch (final ParserException e) {
            plugin.getComponentLogger().warn("Failed to read location of character '{}': {}", name, e.getMessage());
        }
    }

    private class PaperTagOptions implements TagOptions {
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
        public @Nullable Brightness getBrightness() {
            return brightness;
        }

        @Override
        public @Nullable Color getBackgroundColor() {
            return backgroundColor;
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
        public boolean hasTextShadow() {
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
            textDisplayName().ifPresent(display -> display.setAlignment(alignment));
            this.alignment = alignment;
            return true;
        }

        @Override
        public boolean setBackgroundColor(@Nullable final Color color) {
            if (Objects.equals(color, this.backgroundColor)) return false;
            textDisplayName().ifPresent(display -> display.setBackgroundColor(color));
            this.backgroundColor = color;
            return true;
        }

        @Override
        public boolean setBillboard(final Billboard billboard) {
            if (Objects.equals(billboard, this.billboard)) return false;
            textDisplayName().ifPresent(display -> display.setBillboard(billboard));
            this.billboard = billboard;
            return true;
        }

        @Override
        public boolean setBrightness(@Nullable final Brightness brightness) {
            if (Objects.equals(brightness, this.brightness)) return false;
            textDisplayName().ifPresent(display -> display.setBrightness(brightness));
            this.brightness = brightness;
            return true;
        }

        @Override
        public boolean setDefaultBackground(final boolean enabled) {
            if (enabled == defaultBackground) return false;
            textDisplayName().ifPresent(display -> display.setDefaultBackground(enabled));
            this.defaultBackground = enabled;
            return true;
        }

        @Override
        public boolean setLeftRotation(final Quaternionf rotation) {
            if (Objects.equals(rotation, this.leftRotation)) return false;
            this.leftRotation = rotation;
            textDisplayName().ifPresent(display -> display.setTransformation(getTransformation()));
            return true;
        }

        @Override
        public boolean setLineWidth(final int width) {
            if (width == lineWidth) return false;
            textDisplayName().ifPresent(display -> display.setLineWidth(width));
            this.lineWidth = width;
            return true;
        }

        @Override
        public boolean setOffset(final Vector3f offset) {
            if (Objects.equals(offset, this.offset)) return false;
            this.offset = offset;
            textDisplayName().ifPresent(display -> display.setTransformation(getTransformation()));
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
            textDisplayName().ifPresent(display -> display.setTransformation(getTransformation()));
            return true;
        }

        @Override
        public boolean setScale(final Vector3f scale) {
            if (Objects.equals(scale, this.scale)) return false;
            this.scale = scale;
            textDisplayName().ifPresent(display -> display.setTransformation(getTransformation()));
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
            textDisplayName().ifPresent(display -> display.setSeeThrough(seeThrough));
            this.seeThrough = seeThrough;
            return true;
        }

        @Override
        public boolean setTextOpacity(final float opacity) {
            if (opacity == textOpacity) return false;
            this.textOpacity = opacity;
            textDisplayName().ifPresent(PaperCharacter.this::updateTextDisplayNameOpacity);
            return true;
        }

        @Override
        public boolean setTextShadow(final boolean enabled) {
            if (enabled == textShadow) return false;
            textDisplayName().ifPresent(display -> display.setShadowed(enabled));
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

        @Override
        public CompoundTag serialize() throws ParserException {
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

        @Override
        public void deserialize(final CompoundTag tag) throws ParserException {
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
            tag.optional("textShadow").map(Tag::getAsBoolean).ifPresent(this::setTextShadow);
            setBackgroundColor(tag.optional("backgroundColor").map(Tag::getAsInt).map(Color::fromARGB).orElse(null));
            setBrightness(tag.optional("brightness").map(t -> plugin.nbt().deserialize(t, Brightness.class)).orElse(null));
        }
    }
}
