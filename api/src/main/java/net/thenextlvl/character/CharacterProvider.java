package net.thenextlvl.character;

import net.thenextlvl.binder.StaticBinder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a provider responsible for managing, creating, and querying characters.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface CharacterProvider {
    /**
     * Returns the character provider instance.
     *
     * @return the character provider
     * @since 0.9.0
     */
    @Contract(pure = true)
    static CharacterProvider instance() {
        final class Cache {
            private static final CharacterProvider INSTANCE = StaticBinder
                    .getInstance(CharacterProvider.class.getClassLoader())
                    .find(CharacterProvider.class);
        }
        return Cache.INSTANCE;
    }

    /**
     * Get the data folder for the given world
     *
     * @param world the world to get the data folder for
     * @return the data folder for the given world
     * @since 0.9.0
     */
    @Contract(pure = true)
    Path getDataFolder(World world);

    /**
     * Get a character by its entity
     *
     * @param entity the entity of the character
     * @return the character represented by the given entity
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Character> getCharacter(Entity entity);

    /**
     * Get a character by its name
     *
     * @param name the name of the character
     * @return the character with the given name
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Character> getCharacter(String name);

    /**
     * Get a character by the unique id of its entity
     *
     * @param uuid the unique id of the character's entity
     * @return the character represented by the entity with the given unique id
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Character> getCharacter(UUID uuid);

    /**
     * Get all characters
     *
     * @return all characters
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Character> getCharacters();

    /**
     * Get all characters in the given chunk
     *
     * @param chunk the chunk to check
     * @return all characters in the given chunk
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Character> getCharacters(Chunk chunk);

    /**
     * Get all characters visible to the given player
     *
     * @param player the player to check
     * @return all characters visible to the given player
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Character> getCharacters(Player player);

    /**
     * Get all characters in the given world
     *
     * @param world the world to check
     * @return all characters in the given world
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Character> getCharacters(World world);

    /**
     * Get all characters nearby the given location
     *
     * @param location the location to check
     * @param radius   the radius to check
     * @return all characters nearby the given location
     * @throws IllegalArgumentException if the radius is not greater than 0
     * @throws IllegalArgumentException if the world of the location is {@code null}
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<Character> getCharactersNearby(Location location, double radius) throws IllegalArgumentException;

    /**
     * Get the names of all characters
     *
     * @return the names of all characters
     * @since 0.9.0
     */
    @Contract(pure = true)
    Stream<String> getCharacterNames();

    /**
     * Check if a character with the given name exists
     *
     * @param name the name of the character
     * @return {@code true} if the character exists, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean hasCharacter(String name);

    /**
     * Check if the given character exists
     *
     * @param character the character to check
     * @return {@code true} if the character exists, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean hasCharacter(Character character);

    /**
     * Check if the given entity is part of a character
     *
     * @param entity the entity to check
     * @return {@code true} if the entity is part of a character, {@code false} otherwise
     * @see Character#isPart(Entity)
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isCharacterPart(Entity entity);

    /**
     * Create a character with the given name, location and entity type
     * <p>
     * The character is spawned as soon as the chunk at the given location is loaded.
     *
     * @param name     the name of the character
     * @param location the location of the character
     * @param type     the entity type of the character
     * @return the created character
     * @throws IllegalStateException    if a character with the given name already exists
     * @throws IllegalArgumentException if the entity type is not spawnable or the world of the location is {@code null}
     * @since 0.9.0
     */
    @Contract(value = "_, _, _ -> new", mutates = "this")
    Character createCharacter(String name, Location location, Class<? extends Entity> type) throws IllegalStateException, IllegalArgumentException;

    /**
     * Create a character with the given name, location and entity type
     * <p>
     * The character is spawned as soon as the chunk at the given location is loaded.
     *
     * @param name     the name of the character
     * @param location the location of the character
     * @param type     the entity type of the character
     * @return the created character
     * @throws IllegalStateException    if a character with the given name already exists
     * @throws IllegalArgumentException if the entity type is not spawnable or the world of the location is {@code null}
     * @since 0.9.0
     */
    @Contract(value = "_, _, _ -> new", mutates = "this")
    Character createCharacter(String name, Location location, EntityType type) throws IllegalStateException, IllegalArgumentException;

    /**
     * Create a mannequin character with the given name and location
     * <p>
     * The character is spawned as soon as the chunk at the given location is loaded.
     *
     * @param name     the name of the character
     * @param location the location of the character
     * @return the created character
     * @throws IllegalStateException    if a character with the given name already exists
     * @throws IllegalArgumentException if the world of the location is {@code null}
     * @since 0.9.0
     */
    @Contract(value = "_, _ -> new", mutates = "this")
    Character createCharacter(String name, Location location) throws IllegalStateException, IllegalArgumentException;

    /**
     * Delete a character
     * <p>
     * This despawns the character and deletes its data files.
     *
     * @param character the character to delete
     * @return {@code true} if the character was deleted, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(mutates = "this,io,param")
    boolean deleteCharacter(Character character);

    /**
     * Performs the given action for each character.
     *
     * @param action the action to perform
     * @since 0.9.0
     */
    void forEachCharacter(Consumer<? super Character> action);
}
