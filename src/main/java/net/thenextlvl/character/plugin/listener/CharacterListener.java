package net.thenextlvl.character.plugin.listener;

import net.thenextlvl.character.event.player.PlayerClickCharacterEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CharacterListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCharacterClick(PlayerClickCharacterEvent event) {
        // todo: remove, this is just for testing
        if (event.getType().isLeftClick()) {
            event.getCharacter().getPathfinder().ifPresent(pathfinder -> {
                pathfinder.setCanFloat(true);
                pathfinder.setCanOpenDoors(true);
                pathfinder.setCanPassDoors(true);
                pathfinder.moveTo(event.getPlayer().getLocation());
            });
        }

        event.getCharacter().getActions().values().stream()
                .filter(action -> action.isSupportedClickType(event.getType()))
                .forEach(action -> action.invoke(event.getPlayer(), event.getClickedEntity()));
    }
}
