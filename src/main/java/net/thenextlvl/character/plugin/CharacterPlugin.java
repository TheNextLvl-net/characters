package net.thenextlvl.character.plugin;

import com.destroystokyo.paper.profile.ProfileProperty;
import core.io.IO;
import core.nbt.NBTInputStream;
import core.nbt.serialization.NBT;
import core.nbt.serialization.ParserException;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterController;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.SkinPartBuilder;
import net.thenextlvl.character.plugin.controller.PaperCharacterController;
import net.thenextlvl.character.plugin.listener.ConnectionListener;
import net.thenextlvl.character.plugin.listener.EntityListener;
import net.thenextlvl.character.plugin.serialization.CharacterSerializer;
import net.thenextlvl.character.plugin.serialization.ComponentAdapter;
import net.thenextlvl.character.plugin.serialization.EntityTypeAdapter;
import net.thenextlvl.character.plugin.serialization.KeyAdapter;
import net.thenextlvl.character.plugin.serialization.LocationAdapter;
import net.thenextlvl.character.plugin.serialization.ProfilePropertyAdapter;
import net.thenextlvl.character.plugin.serialization.WorldAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.READ;

@NullMarked
public class CharacterPlugin extends JavaPlugin {
    public static final String ISSUES = "https://github.com/TheNextLvl-net/characters/issues/new";
    private final Metrics metrics = new Metrics(this, 24223);
    private final File savesFolder = new File(getDataFolder(), "saves");

    private final NBT nbt = NBT.builder()
            .registerTypeHierarchyAdapter(Character.class, new CharacterSerializer())
            .registerTypeHierarchyAdapter(Component.class, new ComponentAdapter())
            .registerTypeHierarchyAdapter(EntityType.class, new EntityTypeAdapter())
            .registerTypeHierarchyAdapter(Key.class, new KeyAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(ProfileProperty.class, new ProfilePropertyAdapter())
            .registerTypeHierarchyAdapter(World.class, new WorldAdapter(getServer()))
            .build();
    private final PaperCharacterController characterController = new PaperCharacterController(this);

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(CharacterController.class, characterController, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        readAll().forEach(character -> {
            var location = character.getSpawnLocation();
            if (location == null || character.spawn(location)) return;
            getComponentLogger().error("Failed to spawn character {}", character.getName());
        });
        registerListeners();
    }

    @Override
    public void onDisable() {
        characterController.getCharacters().forEach(character -> {
            character.persist();
            character.despawn();
        });
        metrics.shutdown();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }

    public File savesFolder() {
        return savesFolder;
    }

    public NBT nbt() {
        return nbt;
    }

    public PaperCharacterController characterController() {
        return characterController;
    }

    public @Unmodifiable List<Character<?>> readAll() {
        var files = savesFolder.listFiles((file, name) -> name.endsWith(".dat"));
        return files == null ? List.of() : Arrays.stream(files)
                .map(this::readSafe)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
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

    public Character<?> read(File file) throws IOException {
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

    private NBTInputStream stream(IO file) throws IOException {
        return new NBTInputStream(file.inputStream(READ), StandardCharsets.UTF_8);
    }

    private Character<?> read(NBTInputStream inputStream) throws IOException {
        var entry = inputStream.readNamedTag();
        var root = entry.getKey().getAsCompound();
        var name = entry.getValue().orElseThrow(() -> new ParserException("Character misses root name"));
        var type = nbt.fromTag(root.get("type"), EntityType.class);
        var location = root.optional("location").map(tag -> nbt.fromTag(tag, Location.class)).orElse(null);
        var character = type.equals(EntityType.PLAYER)
                ? createPlayerCharacter(root, name)
                : createCharacter(root, name, type);
        character.setSpawnLocation(location);
        return character;
    }

    private Character<?> createCharacter(CompoundTag root, String name, EntityType type) {
        var character = characterController.createCharacter(name, type);
        return deserialize(root, character);
    }

    private PlayerCharacter createPlayerCharacter(CompoundTag root, String name) {
        var uuid = root.optional("uuid").map(tag -> nbt.fromTag(tag, UUID.class)).orElseGet(UUID::randomUUID);
        var character = characterController.createCharacter(name, uuid);
        root.optional("properties").map(Tag::getAsList).map(tags -> tags.stream()
                .map(tag -> nbt.fromTag(tag, ProfileProperty.class))
                .toList()
        ).ifPresent(character.getGameProfile()::setProperties);
        root.optional("listed").map(Tag::getAsBoolean).ifPresent(character::setListed);
        root.optional("realPlayer").map(Tag::getAsBoolean).ifPresent(character::setRealPlayer);
        root.optional("skinParts").map(Tag::getAsByte).ifPresent(raw ->
                character.setSkinParts(SkinPartBuilder.builder().raw(raw).build()));
        return deserialize(root, character);
    }

    private <T extends Character<?>> T deserialize(CompoundTag root, T character) {
        root.optional("collidable").map(Tag::getAsBoolean).ifPresent(character::setCollidable);
        root.optional("displayName").map(tag -> nbt.fromTag(tag, Component.class))
                .ifPresent(character::setDisplayName);
        root.optional("invincible").map(Tag::getAsBoolean).ifPresent(character::setInvincible);
        root.optional("visibleByDefault").map(Tag::getAsBoolean).ifPresent(character::setVisibleByDefault);
        return character;
    }
}
