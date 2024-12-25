package net.thenextlvl.character;

import net.thenextlvl.character.controller.PaperCharacterController;
import net.thenextlvl.character.listener.EntityListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public class CharacterPlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 24223);

    private final PaperCharacterController characterController = new PaperCharacterController(this);

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(CharacterController.class, characterController, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }

    @Override
    public void onDisable() {
        characterController.getCharacters().forEach(Character::remove);
        metrics.shutdown();
    }

    public PaperCharacterController characterController() {
        return characterController;
    }

    public EntityType getEntityTypeByClass(Class<? extends Entity> type) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> type.equals(entityType.getEntityClass()))
                .findAny().orElseThrow();
    }
}
