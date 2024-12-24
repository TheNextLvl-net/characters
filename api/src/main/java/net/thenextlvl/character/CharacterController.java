package net.thenextlvl.character;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.UUID;

@NullMarked
public interface CharacterController {
    <T extends Entity> Character<T> createCharacter(String name, Class<T> type);

    <T extends Entity> Character<T> createCharacter(String name, EntityType type);

    <T extends Entity> Character<T> getCharacter(String name);

    <T extends Entity> Character<T> getCharacter(T entity);

    <T extends Entity> Character<T> getCharacter(UUID uuid);

    <T extends Entity> Character<T> spawnCharacter(String name, Location location, Class<T> type);

    <T extends Entity> Character<T> spawnCharacter(String name, Location location, EntityType type);

    Collection<? extends Character<?>> getCharacters();

    Collection<? extends Character<?>> getCharacters(Player player);

    PlayerCharacter createCharacter(String name);

    PlayerCharacter getCharacter(Player player);

    PlayerCharacter spawnCharacter(String name, Location location);

    boolean isCharacter(Entity entity);
}
