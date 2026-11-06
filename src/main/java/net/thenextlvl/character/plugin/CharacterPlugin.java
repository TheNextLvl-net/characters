package net.thenextlvl.character.plugin;

import com.destroystokyo.paper.profile.ProfileProperty;
import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.ErrorTracker;
import io.papermc.paper.ServerBuildInfo;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.math.Rotations;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.world.WeatheringCopperState;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.binder.StaticBinder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterProvider;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.action.ClickActionFactory;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.goal.Target;
import net.thenextlvl.character.event.CharacterLoadEvent;
import net.thenextlvl.character.plugin.action.SimpleActionTypes;
import net.thenextlvl.character.plugin.action.SimpleClickActionFactory;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import net.thenextlvl.character.plugin.character.PaperCharacterProvider;
import net.thenextlvl.character.plugin.character.PaperSkinFactory;
import net.thenextlvl.character.plugin.command.CharacterCommand;
import net.thenextlvl.character.plugin.listener.CharacterListener;
import net.thenextlvl.character.plugin.listener.ChunkListener;
import net.thenextlvl.character.plugin.listener.ConnectionListener;
import net.thenextlvl.character.plugin.listener.EntityListener;
import net.thenextlvl.character.plugin.listener.WorldListener;
import net.thenextlvl.character.plugin.model.MessageMigrator;
import net.thenextlvl.character.plugin.placeholder.MiniPlaceholdersFormatter;
import net.thenextlvl.character.plugin.placeholder.PlaceholderAPIFormatter;
import net.thenextlvl.character.plugin.serialization.ActionTypeAdapter;
import net.thenextlvl.character.plugin.serialization.AttributesSnapshotAdapter;
import net.thenextlvl.character.plugin.serialization.BlockDataAdapter;
import net.thenextlvl.character.plugin.serialization.BrightnessAdapter;
import net.thenextlvl.character.plugin.serialization.ClickActionAdapter;
import net.thenextlvl.character.plugin.serialization.ColorAdapter;
import net.thenextlvl.character.plugin.serialization.ComponentAdapter;
import net.thenextlvl.character.plugin.serialization.EquipmentSlotGroupAdapter;
import net.thenextlvl.character.plugin.serialization.EquipmentSnapshotAdapter;
import net.thenextlvl.character.plugin.serialization.GoalAdapter;
import net.thenextlvl.character.plugin.serialization.ItemStackAdapter;
import net.thenextlvl.character.plugin.serialization.KeyAdapter;
import net.thenextlvl.character.plugin.serialization.LocationAdapter;
import net.thenextlvl.character.plugin.serialization.NamedTextColorAdapter;
import net.thenextlvl.character.plugin.serialization.ProfilePropertyAdapter;
import net.thenextlvl.character.plugin.serialization.QuaternionfAdapter;
import net.thenextlvl.character.plugin.serialization.RegistryAdapter;
import net.thenextlvl.character.plugin.serialization.ResolvableProfileAdapter;
import net.thenextlvl.character.plugin.serialization.RotationsAdapter;
import net.thenextlvl.character.plugin.serialization.SoundAdapter;
import net.thenextlvl.character.plugin.serialization.TargetAdapter;
import net.thenextlvl.character.plugin.serialization.TitleAdapter;
import net.thenextlvl.character.plugin.serialization.TitleTimesAdapter;
import net.thenextlvl.character.plugin.serialization.TransformationAdapter;
import net.thenextlvl.character.plugin.serialization.Vector3fAdapter;
import net.thenextlvl.character.plugin.serialization.WorldAdapter;
import net.thenextlvl.character.plugin.version.PluginVersionChecker;
import net.thenextlvl.character.property.AttributesSnapshot;
import net.thenextlvl.character.property.EquipmentSnapshot;
import net.thenextlvl.character.skin.SkinFactory;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.nbt.NBTInputStream;
import net.thenextlvl.nbt.serialization.NBT;
import net.thenextlvl.nbt.serialization.adapters.EnumAdapter;
import net.thenextlvl.nbt.tag.Tag;
import org.bstats.bukkit.Metrics;
import org.bukkit.Art;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.CopperGolem;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Sniffer;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieNautilus;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@NullMarked
public final class CharacterPlugin extends JavaPlugin {
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();
    public static final String ISSUES = "https://github.com/TheNextLvl-net/characters/issues/new?template=bug_report.yml";
    public static final boolean RUNNING_FOLIA = ServerBuildInfo.buildInfo().isBrandCompatible(Key.key("papermc", "folia"));

    private final Metrics metrics = new Metrics(this, 24223);
    private final dev.faststats.core.Metrics fastStats = BukkitMetrics.factory()
            .token("873c0bfa3ed19cc26a1a172d008b501b")
            .errorTracker(ERROR_TRACKER)
            .create(this);

    private final Path legacySavesFolder = getDataPath().resolve("saves");
    private final Path translations = getDataPath().resolve("translations");

    private final NBT nbt = NBT.builder()
            .registerTypeHierarchyAdapter(ActionType.class, new ActionTypeAdapter())
            .registerTypeHierarchyAdapter(Art.class, new RegistryAdapter<>(RegistryKey.PAINTING_VARIANT))
            .registerTypeHierarchyAdapter(AttributeModifier.Operation.class, new EnumAdapter<>(AttributeModifier.Operation.class))
            .registerTypeHierarchyAdapter(AttributesSnapshot.class, new AttributesSnapshotAdapter())
            .registerTypeHierarchyAdapter(Axolotl.Variant.class, new EnumAdapter<>(Axolotl.Variant.class))
            .registerTypeHierarchyAdapter(BlockData.class, new BlockDataAdapter(getServer()))
            .registerTypeHierarchyAdapter(BlockFace.class, new EnumAdapter<>(BlockFace.class))
            .registerTypeHierarchyAdapter(Brightness.class, new BrightnessAdapter())
            .registerTypeHierarchyAdapter(Cat.SoundVariant.class, new RegistryAdapter<>(RegistryKey.CAT_SOUND_VARIANT))
            .registerTypeHierarchyAdapter(Cat.Type.class, new RegistryAdapter<>(RegistryKey.CAT_VARIANT))
            .registerTypeHierarchyAdapter(Chicken.SoundVariant.class, new RegistryAdapter<>(RegistryKey.CHICKEN_SOUND_VARIANT))
            .registerTypeHierarchyAdapter(Chicken.Variant.class, new RegistryAdapter<>(RegistryKey.CHICKEN_VARIANT))
            .registerTypeHierarchyAdapter(ClickAction.class, new ClickActionAdapter())
            .registerTypeHierarchyAdapter(Color.class, new ColorAdapter())
            .registerTypeHierarchyAdapter(Component.class, new ComponentAdapter())
            .registerTypeHierarchyAdapter(CopperGolem.State.class, new EnumAdapter<>(CopperGolem.State.class))
            .registerTypeHierarchyAdapter(Cow.SoundVariant.class, new RegistryAdapter<>(RegistryKey.COW_SOUND_VARIANT))
            .registerTypeHierarchyAdapter(Cow.Variant.class, new RegistryAdapter<>(RegistryKey.COW_VARIANT))
            .registerTypeHierarchyAdapter(Display.Billboard.class, new EnumAdapter<>(Display.Billboard.class))
            .registerTypeHierarchyAdapter(DyeColor.class, new EnumAdapter<>(DyeColor.class))
            .registerTypeHierarchyAdapter(EntityEffect.class, new EnumAdapter<>(EntityEffect.class))
            .registerTypeHierarchyAdapter(EntityType.class, new RegistryAdapter<>(RegistryKey.ENTITY_TYPE))
            .registerTypeHierarchyAdapter(EquipmentSlotGroup.class, new EquipmentSlotGroupAdapter())
            .registerTypeHierarchyAdapter(EquipmentSnapshot.class, new EquipmentSnapshotAdapter())
            .registerTypeHierarchyAdapter(Goal.class, new GoalAdapter())
            .registerTypeHierarchyAdapter(Fox.Type.class, new EnumAdapter<>(Fox.Type.class))
            .registerTypeHierarchyAdapter(Frog.Variant.class, new RegistryAdapter<>(RegistryKey.FROG_VARIANT))
            .registerTypeHierarchyAdapter(Horse.Color.class, new EnumAdapter<>(Horse.Color.class))
            .registerTypeHierarchyAdapter(Horse.Style.class, new EnumAdapter<>(Horse.Style.class))
            .registerTypeHierarchyAdapter(ItemDisplay.ItemDisplayTransform.class, new EnumAdapter<>(ItemDisplay.ItemDisplayTransform.class))
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Key.class, new KeyAdapter())
            .registerTypeHierarchyAdapter(Llama.Color.class, new EnumAdapter<>(Llama.Color.class))
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(MainHand.class, new EnumAdapter<>(MainHand.class))
            .registerTypeHierarchyAdapter(MushroomCow.Variant.class, new EnumAdapter<>(MushroomCow.Variant.class))
            .registerTypeHierarchyAdapter(NamedTextColor.class, new NamedTextColorAdapter())
            .registerTypeHierarchyAdapter(Panda.Gene.class, new EnumAdapter<>(Panda.Gene.class))
            .registerTypeHierarchyAdapter(Parrot.Variant.class, new EnumAdapter<>(Parrot.Variant.class))
            .registerTypeHierarchyAdapter(Particle.class, new EnumAdapter<>(Particle.class))
            .registerTypeHierarchyAdapter(Pig.SoundVariant.class, new RegistryAdapter<>(RegistryKey.PIG_SOUND_VARIANT))
            .registerTypeHierarchyAdapter(Pig.Variant.class, new RegistryAdapter<>(RegistryKey.PIG_VARIANT))
            .registerTypeHierarchyAdapter(Pose.class, new EnumAdapter<>(Pose.class))
            .registerTypeHierarchyAdapter(PotionType.class, new EnumAdapter<>(PotionType.class))
            .registerTypeHierarchyAdapter(ProfileProperty.class, new ProfilePropertyAdapter())
            .registerTypeHierarchyAdapter(Quaternionf.class, new QuaternionfAdapter())
            .registerTypeHierarchyAdapter(Rabbit.Type.class, new EnumAdapter<>(Rabbit.Type.class))
            .registerTypeHierarchyAdapter(ResolvableProfile.class, new ResolvableProfileAdapter())
            .registerTypeHierarchyAdapter(Rotation.class, new EnumAdapter<>(Rotation.class))
            .registerTypeHierarchyAdapter(Rotations.class, new RotationsAdapter())
            .registerTypeHierarchyAdapter(Salmon.Variant.class, new EnumAdapter<>(Salmon.Variant.class))
            .registerTypeHierarchyAdapter(Sniffer.State.class, new EnumAdapter<>(Sniffer.State.class))
            .registerTypeHierarchyAdapter(Sound.class, new SoundAdapter())
            .registerTypeHierarchyAdapter(Target.class, new TargetAdapter())
            .registerTypeHierarchyAdapter(Spellcaster.Spell.class, new EnumAdapter<>(Spellcaster.Spell.class))
            .registerTypeHierarchyAdapter(TextDisplay.TextAlignment.class, new EnumAdapter<>(TextDisplay.TextAlignment.class))
            .registerTypeHierarchyAdapter(Title.class, new TitleAdapter())
            .registerTypeHierarchyAdapter(Title.Times.class, new TitleTimesAdapter())
            .registerTypeHierarchyAdapter(Transformation.class, new TransformationAdapter())
            .registerTypeHierarchyAdapter(TriState.class, new EnumAdapter<>(TriState.class))
            .registerTypeHierarchyAdapter(TropicalFish.Pattern.class, new EnumAdapter<>(TropicalFish.Pattern.class))
            .registerTypeHierarchyAdapter(Vector3f.class, new Vector3fAdapter())
            .registerTypeHierarchyAdapter(Villager.Profession.class, new RegistryAdapter<>(RegistryKey.VILLAGER_PROFESSION))
            .registerTypeHierarchyAdapter(Villager.Type.class, new RegistryAdapter<>(RegistryKey.VILLAGER_TYPE))
            .registerTypeHierarchyAdapter(WeatheringCopperState.class, new EnumAdapter<>(WeatheringCopperState.class))
            .registerTypeHierarchyAdapter(Wolf.SoundVariant.class, new RegistryAdapter<>(RegistryKey.WOLF_SOUND_VARIANT))
            .registerTypeHierarchyAdapter(Wolf.Variant.class, new RegistryAdapter<>(RegistryKey.WOLF_VARIANT))
            .registerTypeHierarchyAdapter(World.class, new WorldAdapter(getServer()))
            .registerTypeHierarchyAdapter(ZombieNautilus.Variant.class, new RegistryAdapter<>(RegistryKey.ZOMBIE_NAUTILUS_VARIANT))
            .build();

    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);

    private @Nullable MiniPlaceholdersFormatter miniFormatter = null;
    private @Nullable PlaceholderAPIFormatter papiFormatter = null;

    private final PaperCharacterProvider characterProvider = new PaperCharacterProvider(this);
    private final PaperSkinFactory skinFactory = new PaperSkinFactory(this);
    private final SimpleActionTypes actionTypes = new SimpleActionTypes(this);

    private final Key key = Key.key("characters", "translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .resource("messages.properties", Locale.US)
            .resource("messages_german.properties", Locale.GERMANY)
            .migrator(new MessageMigrator())
            .build();

    public CharacterPlugin() {
        StaticBinder.getInstance(ActionTypes.class.getClassLoader()).bind(ActionTypes.class, actionTypes);
        StaticBinder.getInstance(ClickActionFactory.class.getClassLoader()).bind(ClickActionFactory.class, SimpleClickActionFactory.INSTANCE);
        StaticBinder.getInstance(CharacterProvider.class.getClassLoader()).bind(CharacterProvider.class, characterProvider);
        StaticBinder.getInstance(SkinFactory.class.getClassLoader()).bind(SkinFactory.class, skinFactory);
    }

    @Override
    public void onLoad() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        versionChecker.checkVersion();
    }

    @Override
    public void onDisable() {
        characterProvider.forEachCharacter(character -> {
            character.persist();
            character.despawn();
        });
        metrics.shutdown();
    }

    @Override
    public void onEnable() {
        loadHooks();
        registerCommands();
        registerListeners();
        getServer().getWorlds().forEach(this::loadCharacters);
        warnUnmigratedCharacters();
    }

    private void loadHooks() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            papiFormatter = new PlaceholderAPIFormatter();
            getComponentLogger().info("PlaceholderAPI detected, using for placeholders");
        }
        if (getServer().getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            miniFormatter = new MiniPlaceholdersFormatter();
            getComponentLogger().info("MiniPlaceholders detected, using for placeholders");
        }
    }

    public @Nullable MiniPlaceholdersFormatter miniFormatter() {
        return miniFormatter;
    }

    public @Nullable PlaceholderAPIFormatter papiFormatter() {
        return papiFormatter;
    }

    public void loadCharacters(final World world) {
        migrateLegacyCharacters(world);
        final var dataFolder = characterProvider.getDataFolder(world);
        if (!Files.isDirectory(dataFolder)) return;
        try (final var files = Files.find(dataFolder, 1, (path, attributes) -> {
            return attributes.isRegularFile() && path.getFileName().toString().endsWith(".dat");
        })) {
            files.map(path -> loadSafe(path, world)).filter(Objects::nonNull).forEach(Character::spawn);
        } catch (final IOException e) {
            getComponentLogger().error("Failed to load all characters in world {}", world.key().asString(), e);
            getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ERROR_TRACKER.trackError(e);
        }
    }

    // characters used to be stored in a global folder, they now live in the data folder of their world
    private void migrateLegacyCharacters(final World world) {
        if (!Files.isDirectory(legacySavesFolder)) return;
        final var dataFolder = characterProvider.getDataFolder(world);
        try (final var files = Files.find(legacySavesFolder, 1, (path, attributes) -> {
            return attributes.isRegularFile() && path.getFileName().toString().endsWith(".dat");
        })) {
            files.filter(file -> world.key().equals(readLegacyWorld(file))).forEach(file -> {
                final var target = dataFolder.resolve(file.getFileName());
                final var backup = file.resolveSibling(file.getFileName() + "_old");
                try {
                    if (Files.exists(target)) {
                        getComponentLogger().warn("Cannot migrate character {}, {} already exists", file, target);
                        return;
                    }
                    Files.createDirectories(dataFolder);
                    Files.move(file, target);
                    if (Files.isRegularFile(backup)) Files.move(backup, target.resolveSibling(backup.getFileName()));
                    getComponentLogger().info("Migrated character {} to {}", file.getFileName(), target);
                } catch (final IOException e) {
                    getComponentLogger().error("Failed to migrate character {} to {}", file, target, e);
                    ERROR_TRACKER.trackError(e);
                }
            });
        } catch (final IOException e) {
            getComponentLogger().error("Failed to migrate characters of world {}", world.key().asString(), e);
            ERROR_TRACKER.trackError(e);
        }
    }

    private @Nullable Key readLegacyWorld(final Path file) {
        try (final var inputStream = NBTInputStream.create(file)) {
            return inputStream.readNamedTag().getValue().optional("location")
                    .map(Tag::getAsCompound)
                    .flatMap(location -> location.<Tag>optional("world"))
                    .map(world -> nbt.deserialize(world, Key.class))
                    .orElse(null);
        } catch (final Exception e) {
            getComponentLogger().warn("Failed to read world of legacy character {}", file, e);
            return null;
        }
    }

    private void warnUnmigratedCharacters() {
        if (!Files.isDirectory(legacySavesFolder)) return;
        try (final var files = Files.list(legacySavesFolder)) {
            final var remaining = files.map(Path::getFileName).map(Path::toString)
                    .filter(name -> name.endsWith(".dat")).toList();
            if (remaining.isEmpty()) return;
            getComponentLogger().warn("{} character(s) could not be migrated yet: {}", remaining.size(), String.join(", ", remaining));
            getComponentLogger().warn("They will be migrated once their world is loaded, characters without a location must be recreated");
        } catch (final IOException e) {
            getComponentLogger().warn("Failed to list legacy characters", e);
        }
    }

    public <T> CompletableFuture<T> supply(final Supplier<T> supplier) {
        if (getServer().isGlobalTickThread()) return supplyNow(supplier);

        if (!isEnabled()) {
            final var disabled = new IllegalStateException("Plugin is disabled");
            return CompletableFuture.failedFuture(disabled);
        }

        final var future = new CompletableFuture<T>();
        getServer().getGlobalRegionScheduler().execute(this, () -> complete(future, supplier));
        return future;
    }

    public CompletableFuture<@Nullable Void> supply(final Entity entity, final Runnable runnable) {
        return this.supply(entity, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Runs the supplier on the region that owns the given entity.
     *
     * @return a future that completes with the result, or with {@code null} if the entity was retired before it ran
     */
    public <T> CompletableFuture<@Nullable T> supply(final Entity entity, final Supplier<@Nullable T> supplier) {
        if (getServer().isOwnedByCurrentRegion(entity)) return supplyNow(supplier);

        if (!isEnabled()) {
            final var disabled = new IllegalStateException("Plugin is disabled");
            return CompletableFuture.failedFuture(disabled);
        }

        final var future = new CompletableFuture<@Nullable T>();
        final var task = entity.getScheduler().run(this, ignored -> complete(future, supplier), () -> future.complete(null));
        if (task == null) future.complete(null);
        return future;
    }

    public CompletableFuture<@Nullable Void> supply(final Location location, final Runnable runnable) {
        return this.supply(location, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Runs the supplier on the region that owns the given location.
     */
    public <T> CompletableFuture<@Nullable T> supply(final Location location, final Supplier<@Nullable T> supplier) {
        if (getServer().isOwnedByCurrentRegion(location)) return supplyNow(supplier);

        if (!isEnabled()) {
            final var disabled = new IllegalStateException("Plugin is disabled");
            return CompletableFuture.failedFuture(disabled);
        }

        final var future = new CompletableFuture<@Nullable T>();
        getServer().getRegionScheduler().run(this, location, ignored -> complete(future, supplier));
        return future;
    }

    private <T> CompletableFuture<T> supplyNow(final Supplier<@Nullable T> supplier) {
        final var future = new CompletableFuture<T>();
        complete(future, supplier);
        return future;
    }

    private <T> void complete(final CompletableFuture<@Nullable T> future, final Supplier<@Nullable T> supplier) {
        try {
            future.complete(supplier.get());
        } catch (final Throwable t) {
            future.completeExceptionally(t);
        }
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    public NBT nbt() {
        return nbt;
    }

    public PaperCharacterProvider characterProvider() {
        return characterProvider;
    }

    public PaperSkinFactory skinFactory() {
        return skinFactory;
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(CharacterCommand.create(this), List.of("npc"))));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CharacterListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
    }

    private @Nullable Character loadSafe(final Path file, final World world) {
        try {
            try (final var inputStream = NBTInputStream.create(file)) {
                return load(inputStream, world);
            } catch (final Exception e) {
                final var backup = file.resolveSibling(file.getFileName().toString() + "_old");
                if (!Files.isRegularFile(backup)) throw e;
                getComponentLogger().warn("Failed to load character from {}", file, e);
                getComponentLogger().warn("Falling back to {}", backup);
                try (final var inputStream = NBTInputStream.create(backup)) {
                    return load(inputStream, world);
                }
            }
        } catch (final EOFException e) {
            getComponentLogger().error("The character file {} is irrecoverably broken", file);
            return null;
        } catch (final Exception e) {
            getComponentLogger().error("Failed to load character from {}", file, e);
            getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ERROR_TRACKER.trackError(e);
            return null;
        }
    }

    private @Nullable Character load(final NBTInputStream inputStream, final World world) throws IOException {
        final var entry = inputStream.readNamedTag();
        final var root = entry.getValue();
        final var name = entry.getKey();

        if (characterProvider.hasCharacter(name)) {
            getComponentLogger().warn("A character with the name '{}' is already loaded", name);
            return null;
        }

        var type = nbt.deserialize(root.get("type"), EntityType.class);
        if (type.equals(EntityType.PLAYER)) type = EntityType.MANNEQUIN;

        final var character = new PaperCharacter(this, name, new Location(world, 0, 0, 0), type);
        character.deserialize(root);
        characterProvider.characters.put(name, character);
        new CharacterLoadEvent(character).callEvent();
        return character;
    }
}
