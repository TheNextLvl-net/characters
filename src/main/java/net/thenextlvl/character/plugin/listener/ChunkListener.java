package net.thenextlvl.character.plugin.listener;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ChunkListener implements Listener {
    private final CharacterPlugin plugin;

    public ChunkListener(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(final ChunkLoadEvent event) {
        plugin.characterProvider().getCharacters(event.getChunk()).forEach(Character::spawn);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(final ChunkUnloadEvent event) {
        plugin.characterProvider().getCharacters(event.getChunk()).forEach(Character::despawn);
    }
}
