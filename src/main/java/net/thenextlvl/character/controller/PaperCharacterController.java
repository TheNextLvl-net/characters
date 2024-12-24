package net.thenextlvl.character.controller;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterController;
import net.thenextlvl.character.PlayerCharacter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NullMarked
public class PaperCharacterController implements CharacterController {
    @Override
    public <T extends Entity> Character<T> createCharacter(String name, Class<T> type) {
        return null;
    }

    @Override
    public <T extends Entity> Character<T> createCharacter(String name, EntityType type) {
        return null;
    }

    @Override
    public <T extends Entity> Character<T> getCharacter(String name) {
        return null;
    }

    @Override
    public <T extends Entity> Character<T> getCharacter(T entity) {
        return null;
    }

    @Override
    public <T extends Entity> Character<T> getCharacter(UUID uuid) {
        return null;
    }

    @Override
    public <T extends Entity> Character<T> spawnCharacter(String name, Location location, Class<T> type) {
        return null;
    }

    @Override
    public <T extends Entity> Character<T> spawnCharacter(String name, Location location, EntityType type) {
        return null;
    }

    @Override
    public Collection<? extends Character<?>> getCharacters() {
        return List.of();
    }

    @Override
    public Collection<? extends Character<?>> getCharacters(Player player) {
        return List.of();
    }

    @Override
    public PlayerCharacter createCharacter(String name) {
        return null;
    }

    @Override
    public PlayerCharacter getCharacter(Player player) {
        return null;
    }

    @Override
    public PlayerCharacter spawnCharacter(String name, Location location) {
        return null;
    }

    @Override
    public boolean isCharacter(Entity entity) {
        return false;
    }
}
