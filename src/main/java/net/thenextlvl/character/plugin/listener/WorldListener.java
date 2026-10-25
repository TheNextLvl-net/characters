package net.thenextlvl.character.plugin.listener;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.event.CharacterUnloadEvent;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class WorldListener implements Listener {
    private final CharacterPlugin plugin;

    public WorldListener(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(final WorldLoadEvent event) {
        plugin.loadCharacters(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldSave(final WorldSaveEvent event) {
        plugin.characterProvider().getCharacters(event.getWorld()).forEach(Character::persist);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(final WorldUnloadEvent event) {
        plugin.characterProvider().characters.values().removeIf(character -> {
            if (!character.getWorld().equals(event.getWorld())) return false;
            new CharacterUnloadEvent(character).callEvent();
            character.persist();
            character.despawn();
            return true;
        });
    }
}
