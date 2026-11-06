package net.thenextlvl.character.plugin.listener;

import io.papermc.paper.event.player.PlayerPickEntityEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickType;
import net.thenextlvl.character.event.CharacterDamageEvent;
import net.thenextlvl.character.event.CharacterDeathEvent;
import net.thenextlvl.character.event.CharacterRespawnEvent;
import net.thenextlvl.character.event.PlayerCharacterInteractEvent;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import org.bukkit.GameRules;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
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
    public void onPlayerPickEntity(final PlayerPickEntityEvent event) {
        if (!plugin.characterProvider().isCharacterPart(event.getEntity())) return;
        event.setCancelled(true);
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
        if (event.getEntity().isMarkedInvulnerable()) {
            event.setCancelled(true);
            return;
        }
        plugin.characterProvider().getCharacter(event.getEntity()).ifPresent(character -> {
            final var viewer = ((PaperCharacter) character).getOwner(event.getEntity());
            if (viewer == null) return;
            final var damageEvent = new CharacterDamageEvent(character, viewer, event.getDamageSource(), event.getDamage());
            if (damageEvent.callEvent()) event.setDamage(damageEvent.getDamage());
            else event.setCancelled(true);
        });
    }

    /*
     * Every viewer has their own entity of a character, which no one else sees.
     * Mechanisms like pressure plates, tripwires or sculk sensors would fire for everyone because of an entity
     * only one player sees, so only characters that move alike for all viewers may use them, like doors.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityInteract(final EntityInteractEvent event) {
        if (!usesMechanisms(event.getEntity())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockReceiveGameEvent(final BlockReceiveGameEvent event) {
        final var entity = event.getEntity();
        if (entity != null && !usesMechanisms(entity)) event.setCancelled(true);
    }

    // characters never change blocks, like trampling farmland or turtle eggs
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        if (plugin.characterProvider().isCharacterPart(event.getEntity())) event.setCancelled(true);
    }

    // or freeze water with frost walker
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        if (plugin.characterProvider().isCharacterPart(event.getEntity())) event.setCancelled(true);
    }

    private boolean usesMechanisms(final Entity entity) {
        return plugin.characterProvider().getCharacter(entity)
                .map(character -> ((PaperCharacter) character).movesAlike())
                .orElse(false);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPortal(final EntityPortalEvent event) {
        if (!plugin.characterProvider().isCharacterPart(event.getEntity())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreak(final HangingBreakEvent event) {
        if (!plugin.characterProvider().isCharacterPart(event.getEntity())) return;
        event.setCancelled(event.getEntity().isMarkedInvulnerable());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        plugin.characterProvider().getCharacter(event.getEntity()).ifPresent(character -> {
            final var owner = ((PaperCharacter) character).getOwner(event.getEntity());
            if (owner == null) return;
            new CharacterDeathEvent(character, owner).callEvent();
            // todo: maybe make the respawn delay a per-character property (with an option to never respawn)
            if (!event.getEntity().getWorld().getGameRuleValue(GameRules.IMMEDIATE_RESPAWN)) {
                plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> character.despawn(owner), 20);
                plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> respawn(character, owner), 75);
            } else character.despawn(owner).thenAccept(ignored -> respawn(character, owner));
        });
    }

    private void respawn(final Character character, final Player viewer) {
        if (!viewer.isConnected()) return;
        if (new CharacterRespawnEvent(character, viewer).callEvent()) character.spawn(viewer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRemove(final EntityRemoveEvent event) {
        plugin.characterProvider().getCharacters()
                .filter(character -> character.isPart(event.getEntity()))
                .findAny()
                .map(character -> (PaperCharacter) character)
                .ifPresent(character -> character.invalidate(event.getEntity()));
    }
}
