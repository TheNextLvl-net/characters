package net.thenextlvl.character.plugin.listener;

import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ChunkListener implements Listener {
    private final CharacterPlugin plugin;

    public ChunkListener(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        plugin.characterController().getCharacters(event.getChunk())
                .filter(character -> !character.isSpawned())
                .forEach(character -> character.getSpawnLocation().ifPresent(character::spawn));
    }
}
