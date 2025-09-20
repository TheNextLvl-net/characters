package net.thenextlvl.character.plugin.listener;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.thenextlvl.character.action.ClickType;
import net.thenextlvl.character.event.player.PlayerClickCharacterEvent;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EntityListener implements Listener {
    private final CharacterPlugin plugin;

    public EntityListener(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        plugin.characterController().getCharacter(event.getRightClicked()).ifPresent(character -> {
            var type = event.getPlayer().isSneaking() ? ClickType.SHIFT_RIGHT : ClickType.RIGHT;
            var characterEvent = new PlayerClickCharacterEvent(character, event.getRightClicked(), event.getPlayer(), type);
            characterEvent.callEvent();
            event.setCancelled(true);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPrePlayerAttackEntityLowest(PrePlayerAttackEntityEvent event) {
        plugin.characterController().getCharacter(event.getAttacked()).ifPresent(character -> {
            var type = event.getPlayer().isSneaking() ? ClickType.SHIFT_LEFT : ClickType.LEFT;
            var characterEvent = new PlayerClickCharacterEvent(character, event.getAttacked(), event.getPlayer(), type);
            event.setCancelled(!characterEvent.callEvent() || event.getAttacked().isInvulnerable());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        if (!plugin.characterController().isCharacter(event.getEntity())) return;
        event.setCancelled(event.getEntity().isInvulnerable());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        plugin.characterController().getCharacter(event.getEntity()).ifPresent(character -> {
            if (!Boolean.TRUE.equals(event.getEntity().getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))) {
                plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> character.despawn(), 20);
                plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> character.spawn(), 75);
            } else character.respawn();
        });
    }

    // fixme: the entity data is already removed so persisting here is kinda useless, we need to find something earlier
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRemove(EntityRemoveEvent event) {
        // if (!event.getCause().equals(EntityRemoveEvent.Cause.UNLOAD)) return;
        plugin.characterController().getCharacter(event.getEntity()).ifPresent(entityCharacter -> {
            entityCharacter.persist();
            ((PaperCharacter<?>) entityCharacter).invalidate();
            plugin.getComponentLogger().info("Persisted character {} on {}", entityCharacter.getName(), event.getCause());
        });
    }
}
