package net.thenextlvl.character.plugin.listener;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickType;
import net.thenextlvl.character.event.player.PlayerCharacterClickEvent;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class EntityListener implements Listener {
    private final CharacterPlugin plugin;

    public EntityListener(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        plugin.characterController().getCharacter(event.getRightClicked()).ifPresent(character -> {
            var type = event.getPlayer().isSneaking() ? ClickType.SHIFT_RIGHT : ClickType.RIGHT;
            var characterEvent = new PlayerCharacterClickEvent(character, event.getPlayer(), type);
            characterEvent.setCancelled(true);
            event.setCancelled(!characterEvent.callEvent());
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPrePlayerAttackEntityLowest(PrePlayerAttackEntityEvent event) {
        plugin.characterController().getCharacter(event.getAttacked()).ifPresent(character -> {
            var type = event.getPlayer().isSneaking() ? ClickType.SHIFT_LEFT : ClickType.LEFT;
            var characterEvent = new PlayerCharacterClickEvent(character, event.getPlayer(), type);
            event.setCancelled(!characterEvent.callEvent());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrePlayerAttackEntityHighest(PrePlayerAttackEntityEvent event) {
        plugin.characterController().getCharacter(event.getAttacked())
                .map(Character::isInvincible)
                .ifPresent(event::setCancelled);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        plugin.characterController().getCharacter(event.getEntity())
                .map(Character::isInvincible)
                .ifPresent(event::setCancelled);
    }
}
