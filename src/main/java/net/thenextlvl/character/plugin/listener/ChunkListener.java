package net.thenextlvl.character.plugin.listener;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ChunkListener implements Listener {
    private final CharacterPlugin plugin;

    public ChunkListener(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChunkLoad(final PlayerChunkLoadEvent event) {
        plugin.characterProvider().getCharacters(event.getChunk())
                .forEach(character -> character.spawn(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChunkUnload(final PlayerChunkUnloadEvent event) {
        plugin.characterProvider().getCharacters(event.getChunk())
                .forEach(character -> character.despawn(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(final ChunkUnloadEvent event) {
        plugin.characterProvider().getCharacters(event.getChunk()).forEach(Character::despawn);
    }
}
