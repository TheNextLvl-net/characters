package net.thenextlvl.character.plugin.character;

import com.destroystokyo.paper.entity.Pathfinder;
import com.google.common.base.Preconditions;
import core.io.IO;
import core.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import static org.bukkit.attribute.Attribute.MAX_HEALTH;

@NullMarked
public class PaperCharacter<E extends Entity> implements Character<E> {
    protected final Class<? extends E> entityClass;
    protected final Equipment equipment = new PaperEquipment();
    protected final Map<String, ClickAction<?>> actions = new LinkedHashMap<>();
    protected final Set<Goal> goals = new HashSet<>();
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
    protected @Nullable TextDisplay textDisplayName = null;

    protected Pose pose = Pose.STANDING;

    protected boolean displayNameVisible = true;
    protected boolean pathfinding = false;
    protected boolean persistent = true;
    protected boolean visibleByDefault = true;

    @SuppressWarnings("unchecked")
    public PaperCharacter(CharacterPlugin plugin, String name, EntityType type) {
        Class<? extends Entity> entityClass = type.getEntityClass();
        Preconditions.checkArgument(entityClass != null, "Cannot spawn entity of type %s", type);
        this.entityClass = (Class<? extends E>) entityClass;
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
    public @Unmodifiable Set<Goal> getGoals() {
        return Set.copyOf(goals);
    }

    @Override
    public boolean addGoal(Goal goal) {
        return goals.add(goal);
    }

    @Override
    public boolean removeGoal(Goal goal) {
        return goals.remove(goal);
    }

    @Override
    public <V> Optional<V> getEntity(Class<V> type) {
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
    public @Nullable World getWorld() {
        return getEntity().map(Entity::getWorld).orElse(null);
    }

    @Override
    public Optional<Pathfinder> getPathfinder() {
        return getEntity(Mob.class).map(Mob::getPathfinder);
    }

    @Override
    public <T> boolean addAction(String name, ClickAction<T> action) {
        return action.getActionType().isApplicable(action.getInput(), this)
               && !action.equals(actions.put(name, action));
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
        if (entity == null) return false;
        removeTextDisplayName();
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
                outputStream.writeTag(getName(), plugin.nbt().serialize(this));
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
    public boolean setDisplayName(@Nullable Component displayName) {
        if (Objects.equals(displayName, this.displayName)) return false;
        this.displayName = displayName;
        textDisplayName().ifPresent(this::updateTextDisplayNameText);
        return true;
    }

    @Override
    public boolean setDisplayNameVisible(boolean visible) {
        if (visible == displayNameVisible) return false;
        this.displayNameVisible = visible;
        getEntity().ifPresent(this::updateTextDisplayName);
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
    public boolean setSpawnLocation(@Nullable Location location) {
        if (Objects.equals(location, spawnLocation)) return false;
        this.spawnLocation = location;
        return true;
    }

    @Override
    public boolean setTeamColor(@Nullable NamedTextColor color) {
        if (color == this.teamColor) return false;
        this.teamColor = color;
        textDisplayName().ifPresent(this::updateTextDisplayNameText);
        getEntity().ifPresent(this::updateTeamOptions);
        return true;
    }

    @Override
    public boolean setViewPermission(@Nullable String permission) {
        if (Objects.equals(permission, viewPermission)) return false;
        this.viewPermission = permission;
        getEntity().ifPresent(entity -> plugin.getServer().getOnlinePlayers()
                .forEach(player -> updateVisibility(entity, player)));
        return true;
    }

    @Override
    public boolean setVisibleByDefault(boolean visible) {
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
    @SuppressWarnings("unchecked")
    public CompoundTag serialize() throws ParserException {
        var tag = CompoundTag.empty();
        if (displayName != null) tag.add("displayName", plugin.nbt().serialize(displayName));
        if (spawnLocation != null) tag.add("location", plugin.nbt().serialize(spawnLocation));
        if (teamColor != null) tag.add("teamColor", plugin.nbt().serialize(teamColor));
        if (viewPermission != null) tag.add("viewPermission", viewPermission);
        tag.add("displayNameVisible", displayNameVisible);
        tag.add("equipment", equipment.serialize());
        tag.add("pathfinding", pathfinding);
        tag.add("tagOptions", tagOptions.serialize());
        tag.add("type", plugin.nbt().serialize(type));
        tag.add("visibleByDefault", visibleByDefault);
        var actions = CompoundTag.empty();
        var attributes = CompoundTag.empty();
        this.actions.forEach((name, clickAction) -> actions.add(name, plugin.nbt().serialize(clickAction)));
        if (entity != null) {
            var entityData = new CompoundTag();
            EntityCodecRegistry.registry().codecs().forEach(entityCodec -> {
                if (!entityCodec.entityType().isInstance(entity)) return;
                var codec = (EntityCodec<Object, Object>) entityCodec;
                var object = codec.getter().apply(entity);
                entityData.add(codec.key().asString(), codec.adapter().serialize(object, null)); // fixme: null context
            });
            tag.add("entityData", actions);
        }
        if (!actions.isEmpty()) tag.add("clickActions", actions);
        if (!attributes.isEmpty()) tag.add("attributes", attributes);
        return tag;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserialize(Tag tag) throws ParserException {
        var root = tag.getAsCompound();
        if (entity != null) root.optional("entityData").map(Tag::getAsCompound).ifPresent(entityTag -> {
            EntityCodecRegistry.registry().codecs().forEach(entityCodec -> {
                if (!entityCodec.entityType().isInstance(entity)) return;
                var codec = (EntityCodec<Object, Object>) entityCodec;
                entityTag.optional(entityCodec.key().asString())
                        .map(tag1 -> codec.adapter().deserialize(tag1, null)) // fixme: null context
                        .ifPresent(data -> codec.setter().test(entity, data));
            });
        });
        root.optional("clickActions").map(Tag::getAsCompound).ifPresent(actions -> actions.forEach((name, action) ->
                addAction(name, plugin.nbt().<ClickAction<?>>deserialize(action, ClickAction.class))));
        root.optional("displayName").map(t -> plugin.nbt().deserialize(t, Component.class)).ifPresent(this::setDisplayName);
        root.optional("displayNameVisible").map(Tag::getAsBoolean).ifPresent(this::setDisplayNameVisible);
        root.optional("equipment").ifPresent(equipment::deserialize);
        root.optional("pathfinding").map(Tag::getAsBoolean).ifPresent(this::setPathfinding);
        root.optional("tagOptions").ifPresent(tagOptions::deserialize);
        root.optional("teamColor").map(t -> plugin.nbt().deserialize(t, NamedTextColor.class)).ifPresent(this::setTeamColor);
        root.optional("viewPermission").map(Tag::getAsString).ifPresent(this::setViewPermission);
        root.optional("visibleByDefault").map(Tag::getAsBoolean).ifPresent(this::setVisibleByDefault);
    }

    public void updateVisibility(E entity, Player player) {
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

    protected void preSpawn(E entity) {
        entity.setMetadata("NPC", new FixedMetadataValue(plugin, true));
        entity.setVisibleByDefault(visibleByDefault);

        if (entity instanceof TNTPrimed primed) primed.setFuseTicks(Integer.MAX_VALUE);

        // todo: apply codecs preSpawn and not only on deserialize
        // attributes.forEach(attribute -> {
        //     @SuppressWarnings("unchecked") var type = (AttributeType<Object, Object>) attribute.getType();
        //     type.set(entity, attribute.getValue());
        // });

        if (entity instanceof LivingEntity living) {
            if (living.getEquipment() != null) equipment.getItems().forEach((slot, item) ->
                    living.getEquipment().setItem(slot, item, true));
            living.setCanPickupItems(false);
            var instance = living.getAttribute(MAX_HEALTH);
            if (instance != null) living.setHealth(instance.getValue());
        }

        if (entity instanceof Mob mob) {
            mob.setLootTable(EmptyLootTable.INSTANCE);
            updatePathfinderGoals(mob);
        }

        if (viewPermission != null || !visibleByDefault) plugin.getServer().getOnlinePlayers()
                .forEach(player -> updateVisibility(entity, player));

        updateTextDisplayName(entity);
        updateTeamOptions(entity);
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

    protected void removeTextDisplayName() {
        if (textDisplayName == null) return;
        textDisplayName.remove();
        textDisplayName = null;
    }

    private void updateTextDisplayName(TextDisplay display) {
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

    private void updateTextDisplayNameText(TextDisplay display) {
        var component = displayName == null ? Component.text(getName()) : displayName;
        display.text(component.colorIfAbsent(teamColor));
    }

    private void updateTextDisplayNameOpacity(TextDisplay display) {
        display.setTextOpacity((byte) Math.round(25f + ((100f - tagOptions.getTextOpacity()) * 2.3f)));
    }

    protected void updateTextDisplayName(E entity) {
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

    public void updateTeamOptions(E entity) {
        entity.getTrackedBy().forEach(player -> updateTeamOptions(getCharacterSettingsTeam(player)));
    }

    protected void updateTeamOptions(Team team) {
        team.color(teamColor);
        var collidable = getEntity(LivingEntity.class).map(LivingEntity::isCollidable).orElse(false);
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
            var tag = CompoundTag.empty();
            equipment.forEach((slot, item) -> {
                if (item == null || item.isEmpty()) return;
                tag.add(slot.name(), plugin.nbt().serialize(item));
            });
            return tag;
        }

        @Override
        public void deserialize(Tag tag) throws ParserException {
            slots.forEach(slot -> tag.getAsCompound().optional(slot.name())
                    .map(t -> plugin.nbt().deserialize(t, ItemStack.class))
                    .ifPresent(item -> equipment.put(slot, item)));
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
        public boolean setAlignment(TextAlignment alignment) {
            if (Objects.equals(alignment, this.alignment)) return false;
            textDisplayName().ifPresent(display -> display.setAlignment(alignment));
            this.alignment = alignment;
            return true;
        }

        @Override
        public boolean setBackgroundColor(@Nullable Color color) {
            if (Objects.equals(color, this.backgroundColor)) return false;
            textDisplayName().ifPresent(display -> display.setBackgroundColor(backgroundColor));
            this.backgroundColor = color;
            return true;
        }

        @Override
        public boolean setBillboard(Billboard billboard) {
            if (Objects.equals(billboard, this.billboard)) return false;
            textDisplayName().ifPresent(display -> display.setBillboard(billboard));
            this.billboard = billboard;
            return true;
        }

        @Override
        public boolean setBrightness(@Nullable Brightness brightness) {
            if (Objects.equals(brightness, this.brightness)) return false;
            textDisplayName().ifPresent(display -> display.setBrightness(brightness));
            this.brightness = brightness;
            return true;
        }

        @Override
        public boolean setDefaultBackground(boolean enabled) {
            if (enabled == defaultBackground) return false;
            textDisplayName().ifPresent(display -> display.setDefaultBackground(enabled));
            this.defaultBackground = enabled;
            return true;
        }

        @Override
        public boolean setLeftRotation(Quaternionf rotation) {
            if (Objects.equals(rotation, this.leftRotation)) return false;
            this.leftRotation = rotation;
            textDisplayName().ifPresent(display -> display.setTransformation(getTransformation()));
            return true;
        }

        @Override
        public boolean setLineWidth(int width) {
            if (width == lineWidth) return false;
            textDisplayName().ifPresent(display -> display.setLineWidth(width));
            this.lineWidth = width;
            return true;
        }

        @Override
        public boolean setOffset(Vector3f offset) {
            if (Objects.equals(offset, this.offset)) return false;
            this.offset = offset;
            textDisplayName().ifPresent(display -> display.setTransformation(getTransformation()));
            return true;
        }

        @Override
        public boolean setOffsetX(float offset) {
            return setOffset(new Vector3f(offset, this.offset.y(), this.offset.z()));
        }

        @Override
        public boolean setOffsetY(float offset) {
            return setOffset(new Vector3f(this.offset.x(), offset, this.offset.z()));
        }

        @Override
        public boolean setOffsetZ(float offset) {
            return setOffset(new Vector3f(this.offset.x(), this.offset.y(), offset));
        }

        @Override
        public boolean setRightRotation(Quaternionf rotation) {
            if (Objects.equals(rotation, this.rightRotation)) return false;
            this.rightRotation = rotation;
            textDisplayName().ifPresent(display -> display.setTransformation(getTransformation()));
            return true;
        }

        @Override
        public boolean setScale(Vector3f scale) {
            if (Objects.equals(scale, this.scale)) return false;
            this.scale = scale;
            textDisplayName().ifPresent(display -> display.setTransformation(getTransformation()));
            return true;
        }

        private Transformation getTransformation() {
            return new Transformation(offset, leftRotation, scale, rightRotation);
        }

        @Override
        public boolean setScale(float scale) {
            return setScale(new Vector3f(scale));
        }

        @Override
        public boolean setSeeThrough(boolean seeThrough) {
            if (seeThrough == this.seeThrough) return false;
            textDisplayName().ifPresent(display -> display.setSeeThrough(seeThrough));
            this.seeThrough = seeThrough;
            return true;
        }

        @Override
        public boolean setTextOpacity(float opacity) {
            if (opacity == textOpacity) return false;
            this.textOpacity = opacity;
            textDisplayName().ifPresent(PaperCharacter.this::updateTextDisplayNameOpacity);
            return true;
        }

        @Override
        public boolean setTextShadow(boolean enabled) {
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
        public Tag serialize() throws ParserException {
            var tag = CompoundTag.empty();
            if (backgroundColor != null) tag.add("backgroundColor", backgroundColor.asARGB());
            if (brightness != null) tag.add("brightness", plugin.nbt().serialize(brightness));
            tag.add("alignment", alignment.name());
            tag.add("billboard", billboard.name());
            tag.add("defaultBackground", defaultBackground);
            tag.add("leftRotation", plugin.nbt().serialize(leftRotation));
            tag.add("lineWidth", lineWidth);
            tag.add("offset", plugin.nbt().serialize(offset));
            tag.add("rightRotation", plugin.nbt().serialize(rightRotation));
            tag.add("scale", plugin.nbt().serialize(scale));
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
            root.optional("leftRotation").map(t -> plugin.nbt().deserialize(t, Quaternionf.class)).ifPresent(this::setLeftRotation);
            root.optional("lineWidth").map(Tag::getAsInt).ifPresent(this::setLineWidth);
            root.optional("offset").map(t -> plugin.nbt().deserialize(t, Vector3f.class)).ifPresent(this::setOffset);
            root.optional("rightRotation").map(t -> plugin.nbt().deserialize(t, Quaternionf.class)).ifPresent(this::setRightRotation);
            root.optional("scale").map(t -> plugin.nbt().deserialize(t, Vector3f.class)).ifPresent(this::setScale);
            root.optional("seeThrough").map(Tag::getAsBoolean).ifPresent(this::setSeeThrough);
            root.optional("textOpacity").map(Tag::getAsFloat).ifPresent(this::setTextOpacity);
            root.optional("textShadow").map(Tag::getAsBoolean).ifPresent(this::setTextShadow);
            setBackgroundColor(root.optional("backgroundColor").map(Tag::getAsInt).map(Color::fromARGB).orElse(null));
            setBrightness(root.optional("brightness").map(t -> plugin.nbt().deserialize(t, Brightness.class)).orElse(null));
        }
    }
}
