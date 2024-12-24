package net.thenextlvl.character.listener;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import net.thenextlvl.character.CharacterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CharacterListener implements Listener {
    private final CharacterPlugin plugin;

    public CharacterListener(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerUseUnknownEntityEvent event) {
//        var characters = plugin.characterLoader().getCharacters(event.getPlayer());
//        var interaction = characters.stream()
//                .filter(all -> all.getEntityId() == event.getEntityId())
//                .map(Character::getInteraction)
//                .filter(Objects::nonNull)
//                .findFirst();
//        interaction.ifPresent(value -> value.onInteract(event.isAttack(), event.getHand(), event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        loadNPCs(event.getPlayer(), event.getPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        unloadNPCs(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        updateNPCs(event.getPlayer(), event.getPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        updateNPCs(event.getPlayer(), event.getRespawnLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        updateNPCs(event.getPlayer(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        updateNPCs(event.getPlayer(), event.getTo());
    }

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void onNPCRegister(CharacterRegisterEvent event) {
//        var loader = plugin.characterLoader();
//        event.getCharacter().getLocation().getWorld().getPlayers().stream()
//                .filter(player -> !loader.isLoaded(event.getCharacter(), player))
//                .filter(player -> loader.canSee(player, event.getCharacter()))
//                .forEach(player -> loader.load(event.getCharacter(), player));
//    }

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void onNPCUnregister(CharacterUnregisterEvent event) {
//        var loader = plugin.characterLoader();
//        event.getCharacter().getLocation().getWorld().getPlayers().stream()
//                .filter(player -> loader.isLoaded(event.getCharacter(), player))
//                .forEach(player -> loader.unload(event.getCharacter(), player));
//    }

    private void updateNPCs(Player player, Location location) {
        plugin.characterLoader().getCharacters(player).stream().filter(npc -> {
            var loadingRange = npc.getLoadingRange() * npc.getLoadingRange();
            return npc.getLocation().getWorld() != location.getWorld()
                   || npc.getLocation().distanceSquared(location) > loadingRange;
        }).forEach(npc -> plugin.characterLoader().unload(npc, player));
        loadNPCs(player, location);
    }

    private void reloadNPCs(Player player, Location location) {
        unloadNPCs(player);
        loadNPCs(player, location);
    }

    private void loadNPCs(Player player, Location location) {
        plugin.characterRegistry().getCharacters().stream()
                .filter(npc -> !plugin.characterLoader().isLoaded(npc, player))
                .filter(npc -> plugin.characterLoader().canSee(location, npc))
                .forEach(npc -> {
                    plugin.characterLoader().load(npc, player, location);
                    Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> {
                        if (!plugin.characterLoader().isLoaded(npc, player)) return;
                        if (plugin.characterLoader().isTablistNameHidden(npc, player)) return;
                        plugin.characterLoader().hideTablistName(npc, player);
                    }, 100);
                });
    }

    private void unloadNPCs(Player player) {
        plugin.characterLoader().getCharacters(player).forEach(npc ->
                plugin.characterLoader().unload(npc, player));
    }
}
