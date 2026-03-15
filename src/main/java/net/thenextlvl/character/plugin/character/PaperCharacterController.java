package net.thenextlvl.character.plugin.character;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterController;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@NullMarked
public final class PaperCharacterController implements CharacterController {
    private final CharacterPlugin plugin;
    public final Map<String, Character<?>> characters = new HashMap<>();

    public PaperCharacterController(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T extends Entity> Character<T> createCharacter(final String name, final Class<T> type) {
        return createCharacter(name, getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> createCharacter(final String name, final EntityType type) {
        Preconditions.checkArgument(!characterExists(name), "Character named %s already exists", name);
        final var character = new PaperCharacter<T>(plugin, name, type);
        characters.put(name, character);
        return character;
    }

    @Override
    public <T extends Entity> Character<T> spawnCharacter(final String name, final Location location, final Class<T> type) {
        return spawnCharacter(name, location, getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> spawnCharacter(final String name, final Location location, final EntityType type) {
        final var character = this.<T>createCharacter(name, type);
        character.spawn(location);
        return character;
    }

    @Override
    public Character<Mannequin> createCharacter(final String name) {
        return createCharacter(name, EntityType.MANNEQUIN);
    }

    @Override
    public Character<Mannequin> spawnCharacter(final String name, final Location location) {
        return spawnCharacter(name, location, EntityType.MANNEQUIN);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> Optional<Character<T>> getCharacter(final T entity) {
        return getCharacters().filter(character -> character.getEntity()
                .filter(entity::equals)
                .isPresent()
        ).map(character -> (Character<T>) character).findAny();
    }

    @Override
    public Optional<Character<?>> getCharacter(final String name) {
        return Optional.ofNullable(characters.get(name));
    }

    @Override
    public Optional<Character<?>> getCharacter(final UUID uuid) {
        return getCharacters().filter(character -> character.getEntity()
                .map(Entity::getUniqueId)
                .filter(uuid::equals)
                .isPresent()
        ).findAny();
    }

    @Override
    public Stream<Character<?>> getCharacters() {
        return characters.values().stream();
    }

    @Override
    public Stream<Character<?>> getCharacters(final Chunk chunk) {
        return getCharacters().filter(character -> character.getLocation()
                .or(character::getSpawnLocation)
                .filter(location -> {
                    if (!chunk.getWorld().equals(location.getWorld())) return false;
                    final var chunkX = location.getBlockX() >> 4;
                    final var chunkZ = location.getBlockZ() >> 4;
                    return chunkX == chunk.getX() && chunkZ == chunk.getZ();
                }).isPresent());
    }

    @Override
    public Stream<Character<?>> getCharacters(final Player player) {
        return getCharacters().filter(character -> character.canSee(player));
    }

    @Override
    public Stream<Character<?>> getCharacters(final World world) {
        return getCharacters().filter(character -> character.getWorld().map(world::equals).orElse(false));
    }

    @Override
    public Stream<Character<?>> getCharactersNearby(final Location location, final double radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be greater than 0");
        Preconditions.checkNotNull(location.getWorld(), "World cannot be null");
        final var radiusSquared = radius * radius;
        return getCharacters(location.getWorld()).filter(character -> character.getLocation()
                .map(location1 -> location1.distanceSquared(location) <= radiusSquared)
                .orElse(false));
    }

    @Override
    public @Unmodifiable Set<String> getCharacterNames() {
        return Set.copyOf(characters.keySet());
    }

    @Override
    public boolean characterExists(final String name) {
        return characters.containsKey(name);
    }

    @Override
    public boolean isCharacter(final Entity entity) {
        return getCharacters().anyMatch(character -> character.getEntity().map(entity::equals).orElse(false));
    }

    public void unregister(final String name) {
        characters.remove(name);
    }

    private EntityType getEntityTypeByClass(final Class<? extends Entity> type) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> type.equals(entityType.getEntityClass()))
                .findAny().orElseThrow();
    }
}
