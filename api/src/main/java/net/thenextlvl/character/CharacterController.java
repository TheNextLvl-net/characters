package net.thenextlvl.character;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface CharacterController {
    <T extends Entity> Character<T> createCharacter(String name, Class<T> type);

    <T extends Entity> Character<T> createCharacter(String name, EntityType type);

    <T extends Entity> Character<T> spawnCharacter(String name, Location location, Class<T> type);

    <T extends Entity> Character<T> spawnCharacter(String name, Location location, EntityType type);

    <T extends Entity> Optional<Character<T>> getCharacter(String name);

    <T extends Entity> Optional<Character<T>> getCharacter(T entity);

    <T extends Entity> Optional<Character<T>> getCharacter(UUID uuid);

    @Unmodifiable
    Collection<? extends Character<?>> getCharacters();

    @Unmodifiable
    Collection<? extends Character<?>> getCharacters(Player player);

    @Unmodifiable
    Collection<? extends Character<?>> getCharacters(World world);

    Optional<PlayerCharacter> getCharacter(Player player);

    PlayerCharacter createCharacter(String name);

    PlayerCharacter createCharacter(String name, UUID uuid);

    PlayerCharacter spawnCharacter(String name, Location location);

    boolean isCharacter(Entity entity);
}
