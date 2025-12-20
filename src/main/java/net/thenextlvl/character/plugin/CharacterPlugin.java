package net.thenextlvl.character.plugin;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterProvider;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import net.thenextlvl.character.plugin.character.PaperCharacterController;
import net.thenextlvl.character.plugin.character.PaperSkinFactory;
import net.thenextlvl.character.plugin.character.goal.PaperGoalFactory;
import net.thenextlvl.character.plugin.codec.EntityCodecs;
import net.thenextlvl.character.plugin.command.CharacterCommand;
import net.thenextlvl.character.plugin.listener.CharacterListener;
import net.thenextlvl.character.plugin.listener.ChunkListener;
import net.thenextlvl.character.plugin.listener.ConnectionListener;
import net.thenextlvl.character.plugin.listener.EntityListener;
import net.thenextlvl.character.plugin.model.MessageMigrator;
import net.thenextlvl.character.plugin.serialization.ActionTypeAdapter;
import net.thenextlvl.character.plugin.serialization.AttributeAdapter;
import net.thenextlvl.character.plugin.serialization.BlockDataAdapter;
import net.thenextlvl.character.plugin.serialization.BrightnessAdapter;
import net.thenextlvl.character.plugin.serialization.CatVariantAdapter;
import net.thenextlvl.character.plugin.serialization.ClickActionAdapter;
import net.thenextlvl.character.plugin.serialization.ColorAdapter;
import net.thenextlvl.character.plugin.serialization.ComponentAdapter;
import net.thenextlvl.character.plugin.serialization.EntityTypeAdapter;
import net.thenextlvl.character.plugin.serialization.EquipmentSlotGroupAdapter;
import net.thenextlvl.character.plugin.serialization.FrogVariantAdapter;
import net.thenextlvl.character.plugin.serialization.ItemStackAdapter;
import net.thenextlvl.character.plugin.serialization.KeyAdapter;
import net.thenextlvl.character.plugin.serialization.LocationAdapter;
import net.thenextlvl.character.plugin.serialization.NamedTextColorAdapter;
import net.thenextlvl.character.plugin.serialization.ProfilePropertyAdapter;
import net.thenextlvl.character.plugin.serialization.QuaternionfAdapter;
import net.thenextlvl.character.plugin.serialization.SoundAdapter;
import net.thenextlvl.character.plugin.serialization.TitleAdapter;
import net.thenextlvl.character.plugin.serialization.TitleTimesAdapter;
import net.thenextlvl.character.plugin.serialization.Vector3fAdapter;
import net.thenextlvl.character.plugin.serialization.WorldAdapter;
import net.thenextlvl.character.plugin.version.PluginVersionChecker;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.nbt.NBTInputStream;
import net.thenextlvl.nbt.serialization.NBT;
import net.thenextlvl.nbt.serialization.adapters.EnumAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;
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

@NullMarked
public final class CharacterPlugin extends JavaPlugin implements CharacterProvider {
    public static final String ISSUES = "https://github.com/TheNextLvl-net/characters/issues/new";
    private final Metrics metrics = new Metrics(this, 24223);
    private final Path savesFolder = getDataPath().resolve("saves");
    private final Path translations = getDataPath().resolve("translations");

    private final NBT nbt = NBT.builder()
            .registerTypeHierarchyAdapter(ClickAction.class, new ClickActionAdapter())
            .registerTypeHierarchyAdapter(ActionType.class, new ActionTypeAdapter())
            .registerTypeHierarchyAdapter(AttributeAdapter.class, new AttributeAdapter())
            .registerTypeHierarchyAdapter(AttributeModifier.Operation.class, new EnumAdapter<>(AttributeModifier.Operation.class))
            .registerTypeHierarchyAdapter(BlockData.class, new BlockDataAdapter(getServer()))
            .registerTypeHierarchyAdapter(Brightness.class, new BrightnessAdapter())
            .registerTypeHierarchyAdapter(Cat.Type.class, new CatVariantAdapter())
            .registerTypeHierarchyAdapter(Color.class, new ColorAdapter())
            .registerTypeHierarchyAdapter(Component.class, new ComponentAdapter())
            .registerTypeHierarchyAdapter(DyeColor.class, new EnumAdapter<>(DyeColor.class))
            .registerTypeHierarchyAdapter(EntityEffect.class, new EnumAdapter<>(EntityEffect.class))
            .registerTypeHierarchyAdapter(EntityType.class, new EntityTypeAdapter())
            .registerTypeHierarchyAdapter(EquipmentSlotGroup.class, new EquipmentSlotGroupAdapter())
            .registerTypeHierarchyAdapter(Fox.Type.class, new EnumAdapter<>(Fox.Type.class))
            .registerTypeHierarchyAdapter(Frog.Variant.class, new FrogVariantAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Key.class, new KeyAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(NamedTextColor.class, new NamedTextColorAdapter())
            .registerTypeHierarchyAdapter(Particle.class, new EnumAdapter<>(Particle.class))
            .registerTypeHierarchyAdapter(Pose.class, new EnumAdapter<>(Pose.class))
            .registerTypeHierarchyAdapter(PotionType.class, new EnumAdapter<>(PotionType.class))
            .registerTypeHierarchyAdapter(ProfileProperty.class, new ProfilePropertyAdapter())
            .registerTypeHierarchyAdapter(Quaternionf.class, new QuaternionfAdapter())
            .registerTypeHierarchyAdapter(Sound.class, new SoundAdapter())
            .registerTypeHierarchyAdapter(Title.Times.class, new TitleTimesAdapter())
            .registerTypeHierarchyAdapter(Title.class, new TitleAdapter())
            .registerTypeHierarchyAdapter(TriState.class, new EnumAdapter<>(TriState.class))
            .registerTypeHierarchyAdapter(Vector3f.class, new Vector3fAdapter())
            .registerTypeHierarchyAdapter(World.class, new WorldAdapter(getServer()))
            .build();

    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);

    private final PaperCharacterController characterController = new PaperCharacterController(this);
    private final PaperGoalFactory goalFactory = new PaperGoalFactory(this);
    private final PaperSkinFactory skinFactory = new PaperSkinFactory(this);

    private final Key key = Key.key("characters", "translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .resource("messages.properties", Locale.US)
            .resource("messages_german.properties", Locale.GERMANY)
            .migrator(new MessageMigrator())
            .build();

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(CharacterProvider.class, this, this, ServicePriority.Highest);
        versionChecker.checkVersion();
    }

    @Override
    public void onDisable() {
        characterController.getCharacters().forEach(character -> {
            character.persist();
            character.remove();
        });
        metrics.shutdown();
    }

    @Override
    public void onEnable() {
        EntityCodecs.registerAll();
        registerCommands();
        registerListeners();
        loadAll();
    }

    public void loadAll() {
        try (var files = Files.list(savesFolder)
                .filter(path -> path.getFileName().toString().endsWith(".dat"))) {
            files.map(this::loadSafe).filter(Objects::nonNull).forEach(character -> {
                character.getSpawnLocation().filter(Location::isChunkLoaded).ifPresent(character::spawn);
            });
        } catch (IOException e) {
            getComponentLogger().error("Failed to load all characters", e);
        }
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    public Path savesFolder() {
        return savesFolder;
    }

    public NBT nbt() {
        return nbt;
    }

    @Override
    public PaperCharacterController characterController() {
        return characterController;
    }

    @Override
    public PaperGoalFactory goalFactory() {
        return goalFactory;
    }

    @Override
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
    }

    private @Nullable Character<?> loadSafe(Path file) {
        try {
            try (var inputStream = NBTInputStream.create(file)) {
                return load(inputStream);
            } catch (Exception e) {
                var backup = file.resolveSibling(file.getFileName().toString() + "_old");
                if (!Files.isRegularFile(backup)) throw e;
                getComponentLogger().warn("Failed to load character from {}", file, e);
                getComponentLogger().warn("Falling back to {}", backup);
                try (var inputStream = NBTInputStream.create(backup)) {
                    return load(inputStream);
                }
            }
        } catch (EOFException e) {
            getComponentLogger().error("The character file {} is irrecoverably broken", file);
            return null;
        } catch (Exception e) {
            getComponentLogger().error("Failed to load character from {}", file, e);
            getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            return null;
        }
    }

    private @Nullable Character<?> load(NBTInputStream inputStream) throws IOException {
        var entry = inputStream.readNamedTag();
        var root = entry.getValue();
        var name = entry.getKey();

        if (characterController.characters.containsKey(name)) {
            getComponentLogger().warn("A character with the name '{}' is already loaded", name);
            return null;
        }

        var type = nbt.deserialize(root.get("type"), EntityType.class);
        if (type.equals(EntityType.PLAYER)) type = EntityType.MANNEQUIN;

        var character = new PaperCharacter<>(this, name, type);
        character.deserialize(root);
        characterController.characters.put(name, character);
        return character;
    }
}
