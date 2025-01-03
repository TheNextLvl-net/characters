package net.thenextlvl.character.plugin.listener;

import net.thenextlvl.character.event.player.PlayerClickCharacterEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CharacterListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCharacterClick(PlayerClickCharacterEvent event) {
        event.getCharacter().getActions().values().stream()
                .filter(action -> action.isSupportedClickType(event.getType()))
                .forEach(action -> action.invoke(event.getPlayer()));
    }
}
