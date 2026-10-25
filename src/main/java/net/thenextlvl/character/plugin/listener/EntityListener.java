package net.thenextlvl.character.plugin.listener;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.thenextlvl.character.action.ClickType;
import net.thenextlvl.character.event.PlayerCharacterInteractEvent;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import org.bukkit.GameRules;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EntityListener implements Listener {
    private final CharacterPlugin plugin;

    public EntityListener(final CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractAtEntityEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        plugin.characterProvider().getCharacter(event.getRightClicked()).ifPresent(character -> {
            final var type = event.getPlayer().isSneaking() ? ClickType.SHIFT_RIGHT : ClickType.RIGHT;
            final var characterEvent = new PlayerCharacterInteractEvent(character, event.getPlayer(), type);
            characterEvent.callEvent();
            event.setCancelled(true);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPrePlayerAttackEntity(final PrePlayerAttackEntityEvent event) {
        plugin.characterProvider().getCharacter(event.getAttacked()).ifPresent(character -> {
            final var type = event.getPlayer().isSneaking() ? ClickType.SHIFT_LEFT : ClickType.LEFT;
            final var characterEvent = new PlayerCharacterInteractEvent(character, event.getPlayer(), type);
            event.setCancelled(!characterEvent.callEvent() || event.getAttacked().isMarkedInvulnerable());
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (!plugin.characterProvider().isCharacterPart(event.getEntity())) return;
        event.setCancelled(event.getEntity().isMarkedInvulnerable());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreak(final HangingBreakEvent event) {
        if (!plugin.characterProvider().isCharacterPart(event.getEntity())) return;
        event.setCancelled(event.getEntity().isMarkedInvulnerable());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        plugin.characterProvider().getCharacter(event.getEntity()).ifPresent(character -> {
            if (!event.getEntity().getWorld().getGameRuleValue(GameRules.IMMEDIATE_RESPAWN)) {
                plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> character.despawn(), 20);
                plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> character.spawn(), 75);
            } else character.despawn().thenCompose(ignored -> character.spawn());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRemove(final EntityRemoveEvent event) {
        plugin.characterProvider().getCharacter(event.getEntity()).ifPresent(character -> {
            ((PaperCharacter) character).invalidate();
            // the entity may have walked into another chunk, respawn it if its own chunk is still loaded
            if (event.getCause() == EntityRemoveEvent.Cause.UNLOAD) plugin.getServer().getGlobalRegionScheduler()
                    .runDelayed(plugin, task -> character.spawn(), 1);
        });
    }
}
