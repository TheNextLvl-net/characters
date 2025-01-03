package net.thenextlvl.character.plugin.listener;

import io.papermc.paper.entity.LookAnchor;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class test implements Listener {
    private final CharacterPlugin plugin;

    public test(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        var controller = plugin.characterController();
        var location = event.getPlayer().getEyeLocation();
        controller.getCharacters(event.getPlayer()).forEach(character -> character.getEntity()
                .ifPresent(entity -> entity.lookAt(location, LookAnchor.EYES)));
    }
}
