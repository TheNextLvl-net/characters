package net.thenextlvl.character.plugin.listener;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Spawns characters once the chunk at their location is sent to a player.
 * <p>
 * Goals move the entity of a character away from its location,
 * so an entity is only despawned once it is in a chunk its viewer can no longer see.
 */
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
        final var chunk = event.getChunk();
        final var player = event.getPlayer();
        plugin.characterProvider().getCharacters(chunk.getWorld()).forEach(character ->
                character.getEntity(player).ifPresent(entity -> {
                    final var home = ((PaperCharacter) character).isInChunk(chunk);
                    if (isInChunk(entity, chunk)) {
                        // the entity left the character's location, respawn it there if the player can still see it
                        if (home) character.despawn(player);
                        else character.despawn(player).thenCompose(ignored -> ((PaperCharacter) character).updateVisibility(player));
                    } else if (home && !isVisible(entity, player)) {
                        character.despawn(player);
                    }
                }));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(final ChunkUnloadEvent event) {
        final var chunk = event.getChunk();
        plugin.characterProvider().getCharacters(chunk.getWorld()).forEach(character -> {
            final var home = ((PaperCharacter) character).isInChunk(chunk);
            character.getTrackedBy().toList().forEach(player -> character.getEntity(player).ifPresent(entity -> {
                if (isInChunk(entity, chunk) || (home && !isVisible(entity, player))) character.despawn(player);
            }));
        });
    }

    private static boolean isInChunk(final Entity entity, final Chunk chunk) {
        return Chunk.getChunkKey(entity.getLocation()) == chunk.getChunkKey();
    }

    private static boolean isVisible(final Entity entity, final Player player) {
        return player.isConnected() && player.isChunkSent(Chunk.getChunkKey(entity.getLocation()));
    }
}
