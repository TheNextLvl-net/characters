package net.thenextlvl.character.plugin.character;

import com.google.common.base.Preconditions;
import core.io.IO;
import core.nbt.NBTOutputStream;
import core.nbt.serialization.ParserException;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import core.util.StringUtil;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.attribute.Attribute;
import net.thenextlvl.character.attribute.AttributeType;
import net.thenextlvl.character.attribute.AttributeTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.attribute.PaperAttribute;
import net.thenextlvl.character.plugin.model.EmptyLootTable;
import net.thenextlvl.character.tag.TagOptions;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
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
import static org.bukkit.attribute.Attribute.SCALE;

@NullMarked
public class PaperCharacter<E extends Entity> implements Character<E> {
    protected final Equipment equipment = new PaperEquipment();
    protected final Map<String, ClickAction<?>> actions = new LinkedHashMap<>();
    protected final Set<Attribute<?, ?>> attributes = new HashSet<>();
    protected final Set<UUID> viewers = new HashSet<>();
    protected final String scoreboardName = StringUtil.random(32);
    protected final TagOptions tagOptions = new PaperTagOptions();

    protected final CharacterPlugin plugin;
    protected final EntityType type;
    protected final String name;

    protected @Nullable E entity = null;
    protected @Nullable Component displayName = null;
    protected @Nullable Location spawnLocation = null;
    protected @Nullable NamedTextColor teamColor = null;
    protected @Nullable String viewPermission = null;
    protected @Nullable TextDisplay displayNameHologram;

    protected Pose pose = Pose.STANDING;

    protected boolean ai = false;
    protected boolean displayNameVisible = true;
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
    public ClickAction<?> getAction(String name) {
        return actions.get(name);
    }

    @Override
    public Equipment getEquipment() {
        return equipment;
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
    public Optional<E> getEntity() {
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
    public @Nullable String getViewPermission() {
        return viewPermission;
    }

    @Override
    public @Nullable Location getSpawnLocation() {
        return spawnLocation;
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
    public <T> Optional<T> getAttributeValue(AttributeType<?, T> type) {
        return getAttribute(type).map(Attribute::getValue);
    }

    @Override
    public <T> boolean setAttributeValue(AttributeType<?, T> type, T value) {
        return getAttribute(type).map(attribute -> attribute.setValue(value)).orElse(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V, T> Optional<Attribute<V, T>> getAttribute(AttributeType<V, T> type) {
        return attributes.stream()
                .filter(attribute -> attribute.getType().equals(type))
                .map(attribute -> (Attribute<V, T>) attribute)
                .findAny().or(() -> {
                    if (!type.isApplicable(this)) return Optional.empty();
                    var attribute = new PaperAttribute<>(type, this, plugin);
                    attributes.add(attribute);
                    return Optional.of(attribute);
                });
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
        if (viewPermission != null && !player.hasPermission(viewPermission)) return false;
        return isVisibleByDefault() || isViewer(player.getUniqueId());
    }

    @Override
    public boolean despawn() {
        if (entity == null || !entity.isValid()) return false;
        removeDisplayNameHologram();
        entity.remove();
        entity = null;
        return true;
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
    public boolean isDisplayNameVisible() {
        return displayNameVisible;
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
    public boolean setScale(double scale) {
        if (scale == this.scale) return false;
        getEntity(Attributable.class).map(instance -> instance.getAttribute(SCALE))
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
        getEntity().ifPresent(this::updateDisplayName);
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
    public boolean setViewPermission(@Nullable String permission) {
        if (Objects.equals(permission, viewPermission)) return false;
        this.viewPermission = permission;
        getEntity().ifPresent(entity -> plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (canSee(player)) player.showEntity(plugin, entity);
            else player.hideEntity(plugin, entity);
        }));
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
        this.entity = location.getWorld().spawn(location, (Class<E>) type.getEntityClass(), this::preSpawn);
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

    @Override
    public CompoundTag serialize() throws ParserException {
        var tag = new CompoundTag();
        if (displayName != null) tag.add("displayName", plugin.nbt().toTag(displayName));
        if (spawnLocation != null) tag.add("location", plugin.nbt().toTag(spawnLocation));
        if (teamColor != null) tag.add("teamColor", plugin.nbt().toTag(teamColor));
        if (viewPermission != null) tag.add("viewPermission", viewPermission);
        tag.add("ai", ai);
        tag.add("displayNameVisible", displayNameVisible);
        tag.add("equipment", equipment.serialize());
        tag.add("pathfinding", pathfinding);
        tag.add("scale", scale);
        tag.add("tagOptions", tagOptions.serialize());
        tag.add("ticking", ticking);
        tag.add("type", plugin.nbt().toTag(type));
        tag.add("visibleByDefault", visibleByDefault);
        var actions = new CompoundTag();
        var attributes = new CompoundTag();
        this.actions.forEach((name, clickAction) -> actions.add(name, plugin.nbt().toTag(clickAction)));
        this.attributes.stream().filter(attribute -> attribute.getValue() != null).forEach(attribute ->
                attributes.add(attribute.getType().key().asString(), attribute.serialize()));
        if (!actions.isEmpty()) tag.add("clickActions", actions);
        if (!attributes.isEmpty()) tag.add("attributes", attributes);
        return tag;
    }

    @Override
    public void deserialize(Tag tag) throws ParserException {
        var root = tag.getAsCompound();
        root.optional("ai").map(Tag::getAsBoolean).ifPresent(this::setAI);
        root.optional("attributes").map(Tag::getAsCompound).ifPresent(attributes -> attributes.forEach((name, t) -> {
            @SuppressWarnings("PatternValidation") var key = Key.key(name);
            AttributeTypes.getByKey(key).flatMap(this::getAttribute).ifPresent(attribute -> attribute.deserialize(t));
        }));
        root.optional("clickActions").map(Tag::getAsCompound).ifPresent(actions -> actions.forEach((name, action) ->
                addAction(name, plugin.nbt().fromTag(action, ClickAction.class))));
        root.optional("displayName").map(t -> plugin.nbt().fromTag(t, Component.class)).ifPresent(this::setDisplayName);
        root.optional("displayNameVisible").map(Tag::getAsBoolean).ifPresent(this::setDisplayNameVisible);
        root.optional("equipment").ifPresent(equipment::deserialize);
        root.optional("pathfinding").map(Tag::getAsBoolean).ifPresent(this::setPathfinding);
        root.optional("scale").map(Tag::getAsDouble).ifPresent(this::setScale);
        root.optional("tagOptions").ifPresent(tagOptions::deserialize);
        root.optional("teamColor").map(t -> plugin.nbt().fromTag(t, NamedTextColor.class)).ifPresent(this::setTeamColor);
        root.optional("ticking").map(Tag::getAsBoolean).ifPresent(this::setTicking);
        root.optional("viewPermission").map(Tag::getAsString).ifPresent(this::setViewPermission);
        root.optional("visibleByDefault").map(Tag::getAsBoolean).ifPresent(this::setVisibleByDefault);
    }

    protected void preSpawn(E entity) {
        if (entity instanceof AreaEffectCloud cloud) {
            cloud.setDuration(Tick.tick().fromDuration(Duration.ofDays(999)));
        }
        if (entity instanceof TNTPrimed primed) {
            primed.setFuseTicks(Integer.MAX_VALUE);
        }
        if (entity instanceof Attributable attributable) {
            var scale = attributable.getAttribute(SCALE);
            if (scale != null) scale.setBaseValue(this.scale);
        }
        if (entity instanceof LivingEntity living) {
            if (living.getEquipment() != null) equipment.getItems().forEach((slot, item) ->
                    living.getEquipment().setItem(slot, item, true));
            living.setAI(ai);
            living.setCanPickupItems(false);
        }
        if (entity instanceof Mob mob) {
            mob.setLootTable(EmptyLootTable.INSTANCE);
            updatePathfinderGoals(mob);
        }
        entity.lockFreezeTicks(true);
        entity.setInvulnerable(true);
        entity.setMetadata("NPC", new FixedMetadataValue(plugin, true));
        entity.setPersistent(false);
        entity.setSilent(true);
        entity.setVisibleByDefault(visibleByDefault);
        attributes.forEach(attribute -> {
            @SuppressWarnings("unchecked") var casted = (Attribute<E, Object>) attribute;
            casted.getType().set(entity, attribute.getValue());
        });
        if (viewPermission != null || !visibleByDefault) plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (canSee(player)) player.showEntity(plugin, entity);
            else player.hideEntity(plugin, entity);
        });
        updateDisplayName(entity);
    }

    protected void updateDisplayName(E entity) {
        updateDisplayNameHologram(entity);
        updateTeamOptions();
    }

    protected void updateDisplayNameHologramPosition() {
        if (displayNameHologram == null || entity == null) return;
        displayNameHologram.teleport(getDisplayNameHologramPosition(entity));
    }

    protected void updatePathfinderGoals(Mob mob) {
        if (!pathfinding) plugin.getServer().getMobGoals().removeAllGoals(mob);
    }

    protected Team getCharacterSettingsTeam(Player player) {
        var characterSettings = player.getScoreboard().getTeam(getScoreboardName());
        if (characterSettings != null) return characterSettings;
        characterSettings = player.getScoreboard().registerNewTeam(getScoreboardName());
        characterSettings.addEntry(getScoreboardName());
        return characterSettings;
    }

    protected Location getDisplayNameHologramPosition(E entity) {
        var location = entity.getLocation().clone();
        var incrementor = switch (getAttributeValue(AttributeTypes.ENTITY.POSE).orElse(Pose.STANDING)) {
            case SNEAKING -> 0.15;
            default -> 0.27;
        };
        location.setY(entity.getBoundingBox().getMaxY() + incrementor);
        return location;
    }

    protected void removeDisplayNameHologram() {
        if (displayNameHologram == null) return;
        displayNameHologram.remove();
        displayNameHologram = null;
    }

    protected void spawnDisplayNameHologram(E entity) {
        Preconditions.checkState(displayNameHologram == null, "DisplayNameHologram already spawned");
        var location = getDisplayNameHologramPosition(entity);
        displayNameHologram = entity.getWorld().spawn(location, TextDisplay.class, this::updateDisplayNameHologram);
    }

    protected void updateDisplayNameHologram(TextDisplay display) {
        display.setAlignment(tagOptions.getAlignment());
        display.setBackgroundColor(tagOptions.getBackgroundColor() != null
                ? tagOptions.getBackgroundColor() : Color.fromARGB(1073741824));
        display.setBillboard(tagOptions.getBillboard());
        display.setBrightness(tagOptions.getBrightness());
        display.setDefaultBackground(tagOptions.isDefaultBackground());
        display.setLineWidth(tagOptions.getLineWidth());
        display.setGravity(false);
        display.setPersistent(false);
        display.setSeeThrough(tagOptions.isSeeThrough());
        display.setShadowed(tagOptions.hasTextShadow());
        display.setTeleportDuration(3);
        display.setTextOpacity((byte) Math.round(25f + ((100f - tagOptions.getTextOpacity()) * 2.3f)));
        display.setTransformation(new Transformation(
                display.getTransformation().getTranslation(),
                display.getTransformation().getLeftRotation(),
                tagOptions.getScale(),
                display.getTransformation().getRightRotation()
        ));
        display.setVisibleByDefault(visibleByDefault);
        var component = displayName == null ? Component.text(getName()) : displayName;
        display.text(component.colorIfAbsent(teamColor));
    }

    protected void updateDisplayNameHologram(E entity) {
        if (displayNameHologram == null && showDisplayNameHologram()) {
            spawnDisplayNameHologram(entity);
        } else if (displayNameHologram != null && !showDisplayNameHologram()) {
            removeDisplayNameHologram();
        } else if (displayNameHologram != null) {
            updateDisplayNameHologram(displayNameHologram);
        }
    }

    protected boolean showDisplayNameHologram() {
        return displayName != null && displayNameVisible;
    }

    public void updateTeamOptions() {
        if (entity != null) entity.getTrackedBy().forEach(player ->
                updateTeamOptions(getCharacterSettingsTeam(player)));
    }

    protected void updateTeamOptions(Team team) {
        team.color(teamColor);
        var collidable = getAttributeValue(AttributeTypes.LIVING_ENTITY.COLLIDABLE).orElse(true);
        team.setOption(Team.Option.COLLISION_RULE, collidable ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    private File backupFile() {
        return new File(plugin.savesFolder(), this.name + ".dat_old");
    }

    private File file() {
        return new File(plugin.savesFolder(), this.name + ".dat");
    }

    private class PaperEquipment implements Equipment {
        private final EnumSet<EquipmentSlot> slots = EnumSet.allOf(EquipmentSlot.class);
        private final Map<EquipmentSlot, @Nullable ItemStack> equipment = new HashMap<>();

        @Override
        public @Unmodifiable EnumSet<EquipmentSlot> getSlots() {
            return EnumSet.copyOf(slots);
        }

        @Override
        public @Nullable ItemStack getItem(EquipmentSlot slot) {
            return equipment.get(slot);
        }

        @Override
        public @Unmodifiable Map<EquipmentSlot, @Nullable ItemStack> getItems() {
            return Map.copyOf(equipment);
        }

        @Override
        public boolean clear() {
            if (equipment.isEmpty()) return false;
            getItems().forEach((slot, item) -> equipment.put(slot, null));
            getEntity(LivingEntity.class).map(LivingEntity::getEquipment)
                    .ifPresent(EntityEquipment::clear);
            getEntity(Player.class).ifPresent(player -> player.getTrackedBy().forEach(all ->
                    all.sendEquipmentChange(player, equipment)));
            return true;
        }

        @Override
        public boolean setItem(EquipmentSlot slot, @Nullable ItemStack item) {
            return setItem(slot, item, false);
        }

        @Override
        public boolean setItem(EquipmentSlot slot, @Nullable ItemStack item, boolean silent) {
            Preconditions.checkArgument(slots.contains(slot), "Unsupported slot %s", slot.name());
            var itemStack = item != null && !item.isEmpty() ? item : null;
            if (Objects.equals(itemStack, equipment.put(slot, itemStack))) return false;
            getEntity(LivingEntity.class).map(LivingEntity::getEquipment).
                    ifPresent(equipment -> equipment.setItem(slot, itemStack, silent));
            getEntity(Player.class).ifPresent(player -> player.getTrackedBy().forEach(all ->
                    all.sendEquipmentChange(player, slot, itemStack)));
            return true;
        }

        @Override
        public Tag serialize() throws ParserException {
            var tag = new CompoundTag();
            equipment.forEach((slot, item) -> {
                if (item == null || item.isEmpty()) return;
                tag.add(slot.name(), plugin.nbt().toTag(item));
            });
            return tag;
        }

        @Override
        public void deserialize(Tag tag) throws ParserException {
            slots.forEach(slot -> tag.getAsCompound().optional(slot.name())
                    .map(t -> plugin.nbt().fromTag(t, ItemStack.class))
                    .ifPresent(item -> equipment.put(slot, item)));
        }
    }

    private class PaperTagOptions implements TagOptions {
        private @Nullable Brightness brightness = null;
        private @Nullable Color backgroundColor = null;
        private Billboard billboard = Billboard.CENTER;
        private TextAlignment alignment = TextAlignment.CENTER;
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
        public TextAlignment getAlignment() {
            return alignment;
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
        public boolean setAlignment(TextAlignment alignment) {
            if (Objects.equals(alignment, this.alignment)) return false;
            this.alignment = alignment;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setBackgroundColor(@Nullable Color color) {
            if (Objects.equals(color, this.backgroundColor)) return false;
            this.backgroundColor = color;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setBillboard(Billboard billboard) {
            if (Objects.equals(billboard, this.billboard)) return false;
            this.billboard = billboard;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setBrightness(@Nullable Brightness brightness) {
            if (Objects.equals(brightness, this.brightness)) return false;
            this.brightness = brightness;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setDefaultBackground(boolean enabled) {
            if (enabled == defaultBackground) return false;
            this.defaultBackground = enabled;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setLineWidth(int width) {
            if (width == lineWidth) return false;
            this.lineWidth = width;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setScale(Vector3f vector3f) {
            if (Objects.equals(vector3f, this.scale)) return false;
            this.scale = vector3f;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setSeeThrough(boolean seeThrough) {
            if (seeThrough == this.seeThrough) return false;
            this.seeThrough = seeThrough;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setTextOpacity(float opacity) {
            if (opacity == textOpacity) return false;
            this.textOpacity = opacity;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
            return true;
        }

        @Override
        public boolean setTextShadow(boolean enabled) {
            if (enabled == textShadow) return false;
            this.textShadow = enabled;
            getEntity().ifPresent(PaperCharacter.this::updateDisplayName);
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
        public Tag serialize() throws ParserException {
            var tag = new CompoundTag();
            if (backgroundColor != null) tag.add("backgroundColor", backgroundColor.asARGB());
            if (brightness != null) tag.add("brightness", plugin.nbt().toTag(brightness));
            tag.add("alignment", alignment.name());
            tag.add("billboard", billboard.name());
            tag.add("defaultBackground", defaultBackground);
            tag.add("lineWidth", lineWidth);
            tag.add("scale", plugin.nbt().toTag(scale));
            tag.add("seeThrough", seeThrough);
            tag.add("textOpacity", textOpacity);
            tag.add("textShadow", textShadow);
            return tag;
        }

        @Override
        public void deserialize(Tag tag) throws ParserException {
            var root = tag.getAsCompound();
            root.optional("alignment").map(Tag::getAsString).map(TextAlignment::valueOf).ifPresent(this::setAlignment);
            root.optional("billboard").map(Tag::getAsString).map(Billboard::valueOf).ifPresent(this::setBillboard);
            root.optional("defaultBackground").map(Tag::getAsBoolean).ifPresent(this::setDefaultBackground);
            root.optional("lineWidth").map(Tag::getAsInt).ifPresent(this::setLineWidth);
            root.optional("scale").map(t -> plugin.nbt().fromTag(t, Vector3f.class)).ifPresent(this::setScale);
            root.optional("seeThrough").map(Tag::getAsBoolean).ifPresent(this::setSeeThrough);
            root.optional("textOpacity").map(Tag::getAsFloat).ifPresent(this::setTextOpacity);
            root.optional("textShadow").map(Tag::getAsBoolean).ifPresent(this::setTextShadow);
            setBackgroundColor(root.optional("backgroundColor").map(Tag::getAsInt).map(Color::fromARGB).orElse(null));
            setBrightness(root.optional("brightness").map(t -> plugin.nbt().fromTag(t, Brightness.class)).orElse(null));
        }
    }
}
