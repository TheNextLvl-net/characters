package net.thenextlvl.character.plugin.character.movement;

import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The doors the entities of a character opened in the world.
 * <p>
 * Doors are real blocks every player sees, so they are only opened by characters that move alike for all viewers,
 * see {@link PaperCharacter#movesAlike()}.
 * A door is closed again once no entity of the character is close to it.
 * Opening and closing plays the sound vanilla plays for the door or fence gate,
 * and the entities of the character close to it swing their main hand, every viewer sees their own entity do it.
 */
@NullMarked
public final class Doors {
    private static final double CLOSE_DISTANCE = 2;
    private static final double OPEN_SWING_DISTANCE = 3;
    private static final double CLOSE_SWING_DISTANCE = 4;

    private final Set<Location> open = ConcurrentHashMap.newKeySet();
    private final CharacterPlugin plugin;
    private final PaperCharacter character;

    public Doors(final CharacterPlugin plugin, final PaperCharacter character) {
        this.plugin = plugin;
        this.character = character;
    }

    // opens the closed doors the hitbox of the entity is in, must be called on the region of the entity
    public void open(final Entity entity, final Location position, final double halfWidth) {
        final var world = position.getWorld();
        for (var x = floor(position.getX() - halfWidth); x <= floor(position.getX() + halfWidth); x++) {
            for (var z = floor(position.getZ() - halfWidth); z <= floor(position.getZ() + halfWidth); z++) {
                for (var y = floor(position.getY()); y <= floor(position.getY() + entity.getHeight()); y++) {
                    open(world.getBlockAt(x, y, z));
                }
            }
        }
    }

    private void open(final Block block) {
        if (!PathFinder.isOpenable(block) || PathFinder.isOpen(block)) return;
        if (!plugin.getServer().isOwnedByCurrentRegion(block.getLocation())) return;
        setOpen(block, true);
        // doors are two blocks tall, open the other half too
        if (block.getBlockData() instanceof final Bisected bisected)
            setOpen(block.getRelative(0, bisected.getHalf() == Bisected.Half.BOTTOM ? 1 : -1, 0), true);
        playSound(block, true);
        swingHands(block.getLocation(), OPEN_SWING_DISTANCE);
    }

    // closes the doors on the current region that no entity of the character is close to
    public void close() {
        if (open.isEmpty()) return;
        open.removeIf(location -> {
            if (!plugin.getServer().isOwnedByCurrentRegion(location)) return false;
            if (character.getEntities().anyMatch(entity -> isClose(entity, location))) return false;
            close(location.getBlock());
            return true;
        });
    }

    // closes all doors, regardless of the entities
    public void closeAll() {
        open.forEach(location -> plugin.supply(location, () -> close(location.getBlock())));
        open.clear();
    }

    private void close(final Block block) {
        // both halves of a door are tracked, only play the sound for the lower one
        final var lower = !(block.getBlockData() instanceof final Bisected bisected) || bisected.getHalf() == Bisected.Half.BOTTOM;
        if (!setOpen(block, false) || !lower) return;
        playSound(block, false);
        swingHands(block.getLocation(), CLOSE_SWING_DISTANCE);
    }

    private void swingHands(final Location door, final double distance) {
        final var center = door.clone().add(0.5, 0.5, 0.5);
        character.getEntities().forEach(entity -> {
            if (!(entity instanceof final LivingEntity living)) return;
            if (!entity.getWorld().equals(door.getWorld())) return;
            if (entity.getLocation().distanceSquared(center) > distance * distance) return;
            plugin.supply(entity, living::swingMainHand);
        });
    }

    private boolean setOpen(final Block block, final boolean open) {
        if (!(block.getBlockData() instanceof final Openable openable) || openable.isOpen() == open) return false;
        openable.setOpen(open);
        block.setBlockData(openable, false);
        if (open) this.open.add(block.getLocation());
        return true;
    }

    // the sounds of BlockSetType, the pitch like vanilla
    private static void playSound(final Block block, final boolean open) {
        final var sound = sound(block.getType(), block.getBlockData() instanceof Gate, open);
        final var pitch = ThreadLocalRandom.current().nextFloat() * 0.1f + 0.9f;
        block.getWorld().playSound(block.getLocation().add(0.5, 0.5, 0.5), sound, SoundCategory.BLOCKS, 1, pitch);
    }

    // todo: make this method suck less
    private static Sound sound(final Material material, final boolean gate, final boolean open) {
        final var name = material.name();
        if (name.contains("COPPER")) return open ? Sound.BLOCK_COPPER_DOOR_OPEN : Sound.BLOCK_COPPER_DOOR_CLOSE;
        if (name.startsWith("CRIMSON") || name.startsWith("WARPED")) return gate
                ? open ? Sound.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN : Sound.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE
                : open ? Sound.BLOCK_NETHER_WOOD_DOOR_OPEN : Sound.BLOCK_NETHER_WOOD_DOOR_CLOSE;
        if (name.startsWith("BAMBOO")) return gate
                ? open ? Sound.BLOCK_BAMBOO_WOOD_FENCE_GATE_OPEN : Sound.BLOCK_BAMBOO_WOOD_FENCE_GATE_CLOSE
                : open ? Sound.BLOCK_BAMBOO_WOOD_DOOR_OPEN : Sound.BLOCK_BAMBOO_WOOD_DOOR_CLOSE;
        if (name.startsWith("CHERRY")) return gate
                ? open ? Sound.BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN : Sound.BLOCK_CHERRY_WOOD_FENCE_GATE_CLOSE
                : open ? Sound.BLOCK_CHERRY_WOOD_DOOR_OPEN : Sound.BLOCK_CHERRY_WOOD_DOOR_CLOSE;
        return gate
                ? open ? Sound.BLOCK_FENCE_GATE_OPEN : Sound.BLOCK_FENCE_GATE_CLOSE
                : open ? Sound.BLOCK_WOODEN_DOOR_OPEN : Sound.BLOCK_WOODEN_DOOR_CLOSE;
    }

    private static int floor(final double value) {
        return (int) Math.floor(value);
    }

    private static boolean isClose(final Entity entity, final Location door) {
        final var center = door.clone().add(0.5, 0.5, 0.5);
        return entity.getWorld().equals(door.getWorld())
               && entity.getLocation().distanceSquared(center) <= CLOSE_DISTANCE * CLOSE_DISTANCE;
    }
}
