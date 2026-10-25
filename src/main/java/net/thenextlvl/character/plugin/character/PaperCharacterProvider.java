package net.thenextlvl.character.plugin.character;

import com.google.common.base.Preconditions;
import io.papermc.paper.ServerBuildInfo;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterProvider;
import net.thenextlvl.character.event.CharacterCreateEvent;
import net.thenextlvl.character.event.CharacterDeleteEvent;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.thenextlvl.character.plugin.CharacterPlugin.ISSUES;

@NullMarked
public final class PaperCharacterProvider implements CharacterProvider {
    private static final Set<String> oldVersions = Set.of("1.21.11");
    private final CharacterPlugin plugin;
    public final Map<String, Character> characters = new HashMap<>();

    public PaperCharacterProvider(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Path getDataFolder(final World world) {
        final var version = ServerBuildInfo.buildInfo().minecraftVersionId();
        if (oldVersions.contains(version)) return world.getWorldPath().resolve("characters");
        return world.getWorldPath().resolve("data").resolve("characters");
    }

    @Override
    public Optional<Character> getCharacter(final Entity entity) {
        return getCharacters().filter(character -> character.getEntity()
                .filter(entity::equals)
                .isPresent()
        ).findAny();
    }

    @Override
    public Optional<Character> getCharacter(final String name) {
        return Optional.ofNullable(characters.get(name));
    }

    @Override
    public Optional<Character> getCharacter(final UUID uuid) {
        return getCharacters().filter(character -> character.getEntity()
                .map(Entity::getUniqueId)
                .filter(uuid::equals)
                .isPresent()
        ).findAny();
    }

    @Override
    public Stream<Character> getCharacters() {
        return characters.values().stream();
    }

    @Override
    public Stream<Character> getCharacters(final Chunk chunk) {
        return getCharacters().filter(character -> ((PaperCharacter) character).isInChunk(chunk));
    }

    @Override
    public Stream<Character> getCharacters(final Player player) {
        return getCharacters().filter(character -> character.canSee(player));
    }

    @Override
    public Stream<Character> getCharacters(final World world) {
        return getCharacters().filter(character -> character.getWorld().equals(world));
    }

    @Override
    public Stream<Character> getCharactersNearby(final Location location, final double radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be greater than 0");
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        final var radiusSquared = radius * radius;
        return getCharacters(location.getWorld()).filter(character ->
                character.getLocation().distanceSquared(location) <= radiusSquared);
    }

    @Override
    public Stream<String> getCharacterNames() {
        return getCharacters().map(Character::getName);
    }

    @Override
    public boolean hasCharacter(final String name) {
        return characters.containsKey(name);
    }

    @Override
    public boolean hasCharacter(final Character character) {
        return characters.get(character.getName()) == character;
    }

    @Override
    public boolean isCharacterPart(final Entity entity) {
        return getCharacters().anyMatch(character -> character.isPart(entity));
    }

    @Override
    public Character createCharacter(final String name, final Location location, final Class<? extends Entity> type) {
        return createCharacter(name, location, getEntityTypeByClass(type));
    }

    @Override
    public Character createCharacter(final String name, final Location location, final EntityType type) {
        Preconditions.checkState(!hasCharacter(name), "Character named %s already exists", name);
        final var character = new PaperCharacter(plugin, name, location, type);
        characters.put(name, character);
        new CharacterCreateEvent(character).callEvent();
        character.spawn();
        return character;
    }

    @Override
    public Character createCharacter(final String name, final Location location) {
        return createCharacter(name, location, EntityType.MANNEQUIN);
    }

    @Override
    public boolean deleteCharacter(final Character character) {
        if (!new CharacterDeleteEvent(character).callEvent()) return false;

        character.despawn();
        try {
            Files.deleteIfExists(character.getDataFile());
            Files.deleteIfExists(character.getBackupFile());
        } catch (final IOException e) {
            plugin.getComponentLogger().error("Failed to delete character data: {}", character.getName(), e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            CharacterPlugin.ERROR_TRACKER.trackError(e);
        }
        return characters.remove(character.getName(), character);
    }

    @Override
    public void forEachCharacter(final Consumer<? super Character> action) {
        characters.values().forEach(action);
    }

    private EntityType getEntityTypeByClass(final Class<? extends Entity> type) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> type.equals(entityType.getEntityClass()))
                .findAny().orElseThrow(() -> new IllegalArgumentException("Unknown entity type: " + type.getName()));
    }
}
