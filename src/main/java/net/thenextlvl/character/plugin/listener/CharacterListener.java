package net.thenextlvl.character.plugin.listener;

import net.thenextlvl.character.event.PlayerCharacterInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CharacterListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCharacterInteract(final PlayerCharacterInteractEvent event) {
        // todo: remove, this is just for testing
        if (event.getClickType().isLeftClick()) {
            event.getCharacter().getPathfinder().ifPresent(pathfinder -> {
                pathfinder.setCanFloat(true);
                pathfinder.setCanOpenDoors(true);
                pathfinder.setCanPassDoors(true);
                pathfinder.moveTo(event.getPlayer().getLocation());
            });
        }

        event.getCharacter().forEachAction((name, action) -> {
            if (action.isSupportedClickType(event.getClickType())) action.invoke(event.getCharacter(), event.getPlayer());
        });
    }
}
