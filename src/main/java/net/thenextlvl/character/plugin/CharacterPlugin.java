package net.thenextlvl.character.plugin;

import com.destroystokyo.paper.profile.ProfileProperty;
import core.i18n.file.ComponentBundle;
import core.io.IO;
import core.nbt.NBTInputStream;
import core.nbt.serialization.NBT;
import core.nbt.serialization.ParserException;
import core.nbt.serialization.adapter.EnumAdapter;
import core.nbt.tag.CompoundTag;
import core.paper.messenger.PluginMessenger;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterProvider;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import net.thenextlvl.character.plugin.character.PaperCharacterController;
import net.thenextlvl.character.plugin.character.PaperPlayerCharacter;
import net.thenextlvl.character.plugin.character.PaperSkinFactory;
import net.thenextlvl.character.plugin.character.action.PaperActionType;
import net.thenextlvl.character.plugin.character.action.PaperActionTypeProvider;
import net.thenextlvl.character.plugin.character.goal.PaperGoalFactory;
import net.thenextlvl.character.plugin.codec.EntityCodecs;
import net.thenextlvl.character.plugin.command.CharacterCommand;
import net.thenextlvl.character.plugin.listener.CharacterListener;
import net.thenextlvl.character.plugin.listener.ConnectionListener;
import net.thenextlvl.character.plugin.listener.EntityListener;
import net.thenextlvl.character.plugin.serialization.ActionTypeAdapter;
import net.thenextlvl.character.plugin.serialization.AttributeAdapter;
import net.thenextlvl.character.plugin.serialization.BlockDataAdapter;
import net.thenextlvl.character.plugin.serialization.BrightnessAdapter;
import net.thenextlvl.character.plugin.serialization.CatVariantAdapter;
import net.thenextlvl.character.plugin.serialization.CharacterSerializer;
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
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.papermc.paper.entity.TeleportFlag.EntityState.RETAIN_PASSENGERS;
import static java.nio.file.StandardOpenOption.READ;
import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN;

@NullMarked
public class CharacterPlugin extends JavaPlugin implements CharacterProvider {
    public static final String ISSUES = "https://github.com/TheNextLvl-net/characters/issues/new";
    private final Metrics metrics = new Metrics(this, 24223);
    private final File savesFolder = new File(getDataFolder(), "saves");
    private final Path translations = getDataPath().resolve("translations");

    private final NBT nbt = NBT.builder()
            .registerTypeAdapter(ClickAction.class, new ClickActionAdapter())
            .registerTypeHierarchyAdapter(ActionType.class, new ActionTypeAdapter(this))
            .registerTypeHierarchyAdapter(AttributeAdapter.class, new AttributeAdapter())
            .registerTypeHierarchyAdapter(AttributeModifier.Operation.class, new EnumAdapter<>(AttributeModifier.Operation.class))
            .registerTypeHierarchyAdapter(BlockData.class, new BlockDataAdapter(getServer()))
            .registerTypeHierarchyAdapter(Brightness.class, new BrightnessAdapter())
            .registerTypeHierarchyAdapter(Cat.Type.class, new CatVariantAdapter())
            .registerTypeHierarchyAdapter(Character.class, new CharacterSerializer())
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

    private final PaperActionTypeProvider actionTypeProvider = new PaperActionTypeProvider();
    private final PaperCharacterController characterController = new PaperCharacterController(this);
    private final PaperGoalFactory goalFactory = new PaperGoalFactory(this);
    private final PaperSkinFactory skinFactory = new PaperSkinFactory(this);
    private final PluginMessenger messenger = new PluginMessenger(this);

    public final ActionType<String> sendActionbar = register(new PaperActionType<>("send_actionbar", String.class,
            (player, character, message) -> player.sendActionBar(MiniMessage.miniMessage().deserialize(message,
                    Placeholder.parsed("player", player.getName())))));
    public final ActionType<String> sendMessage = register(new PaperActionType<>("send_message", String.class,
            (player, character, message) -> player.sendMessage(MiniMessage.miniMessage().deserialize(message,
                    Placeholder.parsed("player", player.getName())))));
    public final ActionType<EntityEffect> sendEntityEffect = register(new PaperActionType<>("send_entity_effect", EntityEffect.class,
            (player, character, entityEffect) -> player.sendEntityEffect(entityEffect, character),
            (entityEffect, character) -> entityEffect.isApplicableTo(character.getEntityClass()) && !isDeprecated(entityEffect)));
    public final ActionType<InetSocketAddress> transfer = register(new PaperActionType<>("transfer", InetSocketAddress.class,
            (player, character, address) -> player.transfer(address.getHostName(), address.getPort())));
    public final ActionType<Location> teleport = (register(new PaperActionType<>("teleport", Location.class,
            (player, character, location) -> player.teleportAsync(location, PLUGIN, RETAIN_PASSENGERS))));
    public final ActionType<Sound> playSound = register(new PaperActionType<>("play_sound", Sound.class,
            (player, character, sound) -> player.playSound(sound)));
    public final ActionType<String> runConsoleCommand = register(new PaperActionType<>("run_console_command", String.class,
            (player, character, command) -> player.getServer().dispatchCommand(player.getServer().getConsoleSender(),
                    command.replace("<player>", player.getName()))));
    public final ActionType<String> runCommand = register(new PaperActionType<>("run_command", String.class,
            (player, character, command) -> player.performCommand(command.replace("<player>", player.getName()))));
    public final ActionType<Title> sendTitle = register(new PaperActionType<>("send_title", Title.class,
            (player, character, title) -> player.showTitle(title)));
    public final ActionType<String> connect = register(new PaperActionType<>("connect", String.class,
            (player, character, server) -> messenger.connect(player, server)));

    private final Key key = Key.key("characters", "translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .resource("messages.properties", Locale.US)
            .resource("messages_german.properties", Locale.GERMANY)
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
        readAll().forEach(character -> {
            var location = character.getSpawnLocation();
            if (location.map(character::spawn).orElse(false)) return;
            getComponentLogger().warn("Failed to spawn character {}", character.getName());
        });
        registerCommands();
        registerListeners();
    }

    public @Unmodifiable List<Character<?>> readAll() {
        var files = savesFolder.listFiles((file, name) -> name.endsWith(".dat"));
        return files == null ? List.of() : Arrays.stream(files)
                .map(this::readSafe)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }

    public @Nullable Character<?> read(File file) throws IOException {
        try (var inputStream = stream(IO.of(file))) {
            return read(inputStream);
        } catch (Exception e) {
            var io = IO.of(file.getPath() + "_old");
            if (!io.exists()) throw e;
            getComponentLogger().warn("Failed to load character from {}", file.getPath(), e);
            getComponentLogger().warn("Falling back to {}", io);
            try (var inputStream = stream(io)) {
                return read(inputStream);
            }
        }
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    public File savesFolder() {
        return savesFolder;
    }

    public NBT nbt() {
        return nbt;
    }

    @Override
    public PaperActionTypeProvider actionTypeProvider() {
        return actionTypeProvider;
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
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }

    public boolean isDeprecated(Enum<?> anEnum) {
        try {
            return anEnum.getDeclaringClass().getField(anEnum.name()).isAnnotationPresent(Deprecated.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private @Nullable Character<?> readSafe(File file) {
        try {
            return read(file);
        } catch (EOFException e) {
            getComponentLogger().error("The character file {} is irrecoverably broken", file.getPath());
            return null;
        } catch (Exception e) {
            getComponentLogger().error("Failed to load character from {}", file.getPath(), e);
            getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            return null;
        }
    }

    private NBTInputStream stream(IO file) throws IOException {
        return new NBTInputStream(file.inputStream(READ), StandardCharsets.UTF_8);
    }

    private @Nullable Character<?> read(NBTInputStream inputStream) throws IOException {
        var entry = inputStream.readNamedTag();
        var root = entry.getKey().getAsCompound();
        var name = entry.getValue().orElseThrow(() -> new ParserException("Character misses root name"));
        var type = nbt.deserialize(root.get("type"), EntityType.class);
        Location location;
        try {
            location = root.optional("location").map(tag -> nbt.deserialize(tag, Location.class)).orElse(null);
        } catch (ParserException e) {
            getComponentLogger().warn("Skip loading character '{}': {}", name, e.getMessage());
            return null;
        }
        var character = type.equals(EntityType.PLAYER)
                ? createPlayerCharacter(root, name)
                : createCharacter(root, name, type);
        characterController.characters.put(name, character);
        character.setSpawnLocation(location);
        return character;
    }

    private PlayerCharacter createPlayerCharacter(CompoundTag root, String name) {
        var uuid = root.optional("uuid").map(tag -> nbt.deserialize(tag, UUID.class)).orElseGet(UUID::randomUUID);
        return new PaperPlayerCharacter(this, name, uuid).deserialize(root, nbt); // fixme: null context
    }

    private Character<?> createCharacter(CompoundTag root, String name, EntityType type) {
        return new PaperCharacter<>(this, name, type).deserialize(root, nbt); // fixme: null context
    }

    private <T> ActionType<T> register(ActionType<T> actionType) {
        return actionTypeProvider().register(actionType);
    }
}
