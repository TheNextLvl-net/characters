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
public class ConnectionListener implements Listener {
    private final CharacterPlugin plugin;

    public ConnectionListener(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        loadCharacters(event.getPlayer().getWorld(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadCharacters(event.getPlayer().getWorld(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.characterController().getCharacters().forEach(character ->
                character.getActions().values().forEach(action ->
                        action.resetCooldown(event.getPlayer())));
    }

    private void loadCharacters(World world, Player player) {
        plugin.characterController().getCharacters(world)
                .map(character -> (PaperCharacter<?>) character)
                .forEach(character -> character.loadCharacter(player));
    }
}
