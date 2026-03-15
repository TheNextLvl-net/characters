package net.thenextlvl.character.plugin.listener;

import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ConnectionListener implements Listener {
    private final CharacterPlugin plugin;

    public ConnectionListener(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        loadCharacters(event.getPlayer().getWorld(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        loadCharacters(event.getPlayer().getWorld(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        plugin.characterController().getCharacters().forEach(character ->
                character.getActions().values().forEach(action ->
                        action.resetCooldown(event.getPlayer())));
    }

    private void loadCharacters(final World world, final Player player) {
        plugin.characterController().getCharacters(world)
                .map(character -> (PaperCharacter<?>) character)
                .forEach(character -> character.loadCharacter(player));
    }
}
