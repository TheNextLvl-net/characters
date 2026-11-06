package net.thenextlvl.character.plugin.listener;

import net.thenextlvl.character.event.PlayerCharacterInteractEvent;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CharacterListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCharacterInteract(final PlayerCharacterInteractEvent event) {
        ((PaperCharacter) event.getCharacter()).interact(event.getPlayer());
        event.getCharacter().forEachAction((name, action) -> {
            if (action.isSupportedClickType(event.getClickType())) action.invoke(event.getCharacter(), event.getPlayer());
        });
    }
}
