package net.thenextlvl.character.plugin.controller;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterController;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.model.PaperCharacter;
import net.thenextlvl.character.plugin.model.PaperPlayerCharacter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class PaperCharacterController implements CharacterController {
    private final CharacterPlugin plugin;
    private final Map<String, Character<?>> characters = new HashMap<>();

    public PaperCharacterController(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T extends Entity> Character<T> createCharacter(String name, Class<T> type) {
        return createCharacter(name, getEntityTypeByClass(type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> Character<T> createCharacter(String name, EntityType type) {
        if (type.equals(EntityType.PLAYER)) return (Character<T>) createCharacter(name);
        Preconditions.checkArgument(!characterExists(name), "Character named %s already exists", name);
        var character = new PaperCharacter<T>(plugin, name, type);
        characters.put(name, character);
        return character;
    }

    @Override
    public <T extends Entity> Character<T> spawnCharacter(String name, Location location, Class<T> type) {
        return spawnCharacter(name, location, getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> spawnCharacter(String name, Location location, EntityType type) {
        var character = this.<T>createCharacter(name, type);
        character.spawn(location);
        return character;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> Optional<Character<T>> getCharacter(String name) {
        return Optional.ofNullable(characters.get(name))
                .map(character -> (Character<T>) character);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> Optional<Character<T>> getCharacter(T entity) {
        return characters.values().stream()
                .filter(character -> character.getType().equals(entity.getType()))
                .filter(character -> character.getEntity()
                        .filter(entity::equals)
                        .isPresent()
                ).map(character -> (Character<T>) character)
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> Optional<Character<T>> getCharacter(UUID uuid) {
        return characters.values().stream()
                .filter(character -> character.getEntity()
                        .map(Entity::getUniqueId)
                        .filter(uuid::equals)
                        .isPresent()
                ).map(character -> (Character<T>) character)
                .findFirst();
    }

    @Override
    public @Unmodifiable Collection<? extends Character<?>> getCharacters() {
        return List.copyOf(characters.values());
    }

    @Override
    public @Unmodifiable Collection<? extends Character<?>> getCharacters(Player player) {
        return characters.values().stream()
                .filter(character -> character.canSee(player))
                .toList();
    }

    @Override
    public @Unmodifiable Collection<? extends Character<?>> getCharacters(World world) {
        return characters.values().stream()
                .filter(character -> world.equals(character.getWorld()))
                .toList();
    }

    @Override
    public Optional<PlayerCharacter> getCharacter(Player player) {
        return characters.values().stream()
                .filter(character -> character.getType().equals(EntityType.PLAYER))
                .filter(character -> character.getEntity()
                        .filter(player::equals)
                        .isPresent()
                ).map(character -> (PlayerCharacter) character)
                .findFirst();
    }

    @Override
    public PlayerCharacter createCharacter(String name) {
        return createCharacter(name, UUID.randomUUID());
    }

    @Override
    public PlayerCharacter createCharacter(String name, UUID uuid) {
        Preconditions.checkArgument(!characterExists(name), "Character named %s already exists", name);
        var character = new PaperPlayerCharacter(plugin, name, uuid);
        characters.put(name, character);
        return character;
    }

    @Override
    public PlayerCharacter spawnCharacter(String name, Location location) {
        var character = createCharacter(name);
        character.spawn(location);
        return character;
    }

    @Override
    public boolean characterExists(String name) {
        return characters.containsKey(name);
    }

    @Override
    public boolean isCharacter(Entity entity) {
        return characters.values().stream().anyMatch(character ->
                character.getEntity().map(entity::equals).orElse(false));
    }

    public void unregister(String name) {
        characters.remove(name);
    }

    private EntityType getEntityTypeByClass(Class<? extends Entity> type) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> type.equals(entityType.getEntityClass()))
                .findAny().orElseThrow();
    }
}
