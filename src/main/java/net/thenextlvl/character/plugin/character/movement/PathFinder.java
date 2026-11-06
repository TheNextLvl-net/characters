package net.thenextlvl.character.plugin.character.movement;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A* pathfinding over the blocks of a world, modelled after vanilla's {@code WalkNodeEvaluator},
 * independent of the entity type.
 * <p>
 * Nodes are the blocks an entity can stand or swim in: the block and the blocks above it up to the entity height
 * are passable, water, an open door or, if the entity may open doors, a door that can be opened by hand,
 * and the block below has a collision box, unless the entity swims.
 * An entity can jump up one block, drop down {@link #MAX_FALL} blocks and swim depending on its {@link Swimming} ability.
 * Entities that can swim also drop into water from any height up to {@link #MAX_WATER_FALL} blocks, as it breaks their fall.
 * Ladders, vines and scaffolding can be climbed up and down by every entity, like in vanilla.
 * Entities that climb walls, like spiders, can also be in any block next to a wall, to climb up, down and along it.
 * Entities that fly can be in any block they fit into and move in all directions, like vanilla's {@code FlyNodeEvaluator}.
 * <p>
 * Like in vanilla, every node has a path type with a malus that is added to its cost:
 * lava and powder snow are blocked, fire and damaging blocks are avoided, as are blocks next to them,
 * and water is avoided by entities that do not dive.
 * Only chunks that are loaded and owned by the current region are considered,
 * so this must be called on the region of the entity.
 */
@NullMarked
public final class PathFinder {
    public static final int MAX_FALL = 3;
    public static final int MAX_WATER_FALL = 32;
    public static final double MAX_STEP_UP = 1.0;
    // vanilla visits at most 16 nodes per block of follow range
    public static final int NODES_PER_BLOCK = 16;
    private static final int DEFAULT_MAX_NODES = 16 * NODES_PER_BLOCK;
    // the height of the water surface within the topmost water block
    private static final double WATER_SURFACE = 0.9;

    // the malus of the vanilla path types
    private static final float BLOCKED = -1;
    private static final float WATER = 8;
    private static final float DANGER = 8;
    private static final float DAMAGE_OTHER = 8;
    private static final float DAMAGE_FIRE = 16;
    private static final float STICKY_HONEY = 8;

    private static final int[][] DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    private final Map<Long, Float> malus = new HashMap<>();
    private final Server server;
    private final World world;
    private final int height;
    private final double eyeHeight;
    private final boolean openDoors;
    private final Swimming swimming;
    private final int maxNodes;
    private final boolean climbs;
    private final boolean flies;

    /**
     * How an entity moves in water.
     */
    public enum Swimming {
        /**
         * Swims anywhere in the water, like fish.
         */
        DIVE,
        /**
         * Swims at the surface, with its eyes above the water.
         */
        SURFACE,
        /**
         * Can not swim, sinks and walks along the ground.
         */
        SINK
    }

    /**
     * @param server       the server, used to check region ownership
     * @param world        the world to find paths in
     * @param entityHeight the height of the entity, to check the clearance
     * @param eyeHeight    the eye height of the entity, to keep its eyes above water
     * @param openDoors    whether the entity may open closed doors
     * @param swimming     how the entity moves in water
     * @param followRange  the follow range of the entity, limiting the visited nodes like in vanilla, or {@code 0} for the default
     * @param climbs       whether the entity climbs walls, like spiders
     * @param flies        whether the entity flies
     */
    public PathFinder(final Server server, final World world, final double entityHeight, final double eyeHeight,
                      final boolean openDoors, final Swimming swimming, final double followRange,
                      final boolean climbs, final boolean flies) {
        this.server = server;
        this.world = world;
        this.height = Math.max(1, (int) Math.ceil(entityHeight));
        this.eyeHeight = eyeHeight;
        this.openDoors = openDoors;
        this.swimming = swimming;
        this.maxNodes = followRange > 0 ? (int) (followRange * NODES_PER_BLOCK) : DEFAULT_MAX_NODES;
        this.climbs = climbs;
        this.flies = flies;
    }

    /**
     * A path to walk.
     *
     * @param nodes   the bottom centers of the nodes to walk through, excluding the start
     * @param reaches whether the path reaches the goal, otherwise it ends at the node closest to it
     */
    public record Path(List<Location> nodes, boolean reaches) {
        public static final Path EMPTY = new Path(List.of(), false);
    }

    /**
     * Finds a path from one location to another that has to reach the goal exactly.
     *
     * @param from the start
     * @param to   the goal
     * @return the nodes of the path
     * @see #find(Location, Location, int)
     */
    public List<Location> find(final Location from, final Location to) {
        return find(from, to, 0).nodes();
    }

    /**
     * Finds a path from one location to another.
     * <p>
     * Like in vanilla, the goal is reached once a node is within the given accuracy of it,
     * and if it can not be reached, the path leads to the reachable node closest to it.
     *
     * @param from     the start
     * @param to       the goal
     * @param accuracy the manhattan distance in blocks a node may have to the goal to reach it
     * @return the path, empty if no step brings the entity closer
     */
    public Path find(final Location from, final Location to, final int accuracy) {
        final var start = node(from.getBlockX(), from.getBlockY(), from.getBlockZ());
        final var goal = node(to.getBlockX(), to.getBlockY(), to.getBlockZ());

        final var open = new PriorityQueue<Node>();
        final var parents = new HashMap<Long, Long>();
        final var costs = new HashMap<Long, Double>();
        final var closed = new HashSet<Long>();

        open.add(new Node(start, 0, distance(start, goal)));
        costs.put(start, 0d);
        var closest = start;
        var closestDistance = distance(start, goal);

        while (!open.isEmpty() && closed.size() < maxNodes) {
            final var current = open.poll();
            if (!closed.add(current.key())) continue;
            if (manhattan(current.key(), goal) <= accuracy) return new Path(path(parents, current.key()), true);

            final var distance = distance(current.key(), goal);
            if (distance < closestDistance) {
                closest = current.key();
                closestDistance = distance;
            }

            for (final var neighbor : neighbors(current.key())) {
                if (closed.contains(neighbor)) continue;
                final var cost = costs.get(current.key()) + distance(current.key(), neighbor) + malus(neighbor);
                final var known = costs.get(neighbor);
                if (known != null && known <= cost) continue;
                costs.put(neighbor, cost);
                parents.put(neighbor, current.key());
                open.add(new Node(neighbor, cost, cost + distance(neighbor, goal)));
            }
        }
        return new Path(path(parents, closest), false);
    }

    /**
     * Returns the height an entity stands at in the given block, or {@code null} if it can not stand there.
     *
     * @param x block x
     * @param y block y
     * @param z block z
     * @return the standing height
     */
    public @Nullable Double standingHeight(final int x, final int y, final int z) {
        if (!isClear(x, y, z) || y <= world.getMinHeight()) return null;
        // flying entities do not need ground below them
        if (flies) return (double) y;
        if (isWater(world.getBlockAt(x, y, z))) switch (swimming) {
            case DIVE -> {
                return (double) y;
            }
            case SURFACE -> {
                // float high enough to keep the eyes above the water
                if (isWater(world.getBlockAt(x, y + 1, z))) return null;
                return y + Math.max(0, WATER_SURFACE - eyeHeight + 0.1);
            }
            case SINK -> {
                // walks along the ground like on land
            }
        }
        final var below = world.getBlockAt(x, y - 1, z);
        if (below.isPassable() || below.isLiquid()) return null;
        // ladders and vines have a collision box, but nothing to stand on, an entity on top of one hangs in it
        if (isClimbable(below)) return null;
        return below.getBoundingBox().getMaxY();
    }

    /**
     * Finds the closest height an entity can stand at in the given column, searching around the given height.
     *
     * @param x     block x
     * @param y     block y to start searching at
     * @param z     block z
     * @param range the number of blocks to search up and down
     * @return the standing location, or {@code null} if there is none
     */
    public @Nullable Location ground(final int x, final int y, final int z, final int range) {
        for (var offset = 0; offset <= range; offset++) {
            for (final var dy : offset == 0 ? new int[]{0} : new int[]{offset, -offset}) {
                final var standing = standingHeight(x, y + dy, z);
                if (standing != null && malus(node(x, y + dy, z)) != BLOCKED) {
                    return new Location(world, x + 0.5, standing, z + 0.5);
                }
            }
        }
        return null;
    }

    /**
     * Checks if the given block is water an entity can swim in.
     *
     * @param block the block
     * @return {@code true} if the block is water
     */
    public static boolean isWater(final Block block) {
        final var type = block.getType();
        return type == Material.WATER || type == Material.BUBBLE_COLUMN;
    }

    /**
     * Checks if the given block is a door or fence gate that can be opened by hand.
     *
     * @param block the block
     * @return {@code true} if the block can be opened by hand
     */
    public static boolean isOpenable(final Block block) {
        final var data = block.getBlockData();
        if (data instanceof Gate) return true;
        return data instanceof Door && block.getType() != Material.IRON_DOOR;
    }

    /**
     * Checks if the given block is an open door or fence gate.
     *
     * @param block the block
     * @return {@code true} if the block is open
     */
    public static boolean isOpen(final Block block) {
        return block.getBlockData() instanceof final Openable openable && openable.isOpen()
               && (openable instanceof Door || openable instanceof Gate);
    }

    /**
     * Checks if the given block can be climbed, like ladders, vines and scaffolding.
     *
     * @param block the block
     * @return {@code true} if the block can be climbed
     */
    public static boolean isClimbable(final Block block) {
        return switch (block.getType()) {
            case LADDER, VINE, SCAFFOLDING, WEEPING_VINES, WEEPING_VINES_PLANT, TWISTING_VINES, TWISTING_VINES_PLANT,
                 CAVE_VINES, CAVE_VINES_PLANT -> true;
            default -> false;
        };
    }

    // whether an entity can be inside the given block
    private boolean canOccupy(final Block block) {
        if (block.isLiquid()) return isWater(block);
        if (isClimbable(block)) return true;
        if (block.isPassable() || isOpen(block)) return true;
        return openDoors && isOpenable(block);
    }

    private List<Long> neighbors(final long node) {
        if (flies) return flyingNeighbors(node);
        final var x = x(node);
        final var y = y(node);
        final var z = z(node);
        final var current = standingHeight(x, y, z);
        final var base = current != null ? current : y;

        final var neighbors = new ArrayList<Long>(10);
        // climbing up and down ladders
        if (isOnLadder(x, y, z) || isOnLadder(x, y + 1, z)) addIfPassable(neighbors, x, y + 1, z);
        if (isOnLadder(x, y - 1, z)) addIfPassable(neighbors, x, y - 1, z);
        // climbing up and down walls
        if (climbs) {
            if (isOnWall(x, y + 1, z) && isClear(x, y + height, z)) addIfPassable(neighbors, x, y + 1, z);
            if (isOnWall(x, y - 1, z)) addIfPassable(neighbors, x, y - 1, z);
        }
        // swimming up and down
        if (isWater(world.getBlockAt(x, y, z))) {
            addIfPassable(neighbors, x, y + 1, z);
            if (isWater(world.getBlockAt(x, y - 1, z))) addIfPassable(neighbors, x, y - 1, z);
        }
        for (final var direction : DIRECTIONS) {
            final var nx = x + direction[0];
            final var nz = z + direction[1];
            // diagonals must not cut corners
            if (direction[0] != 0 && direction[1] != 0
                && (!isClear(x + direction[0], y, z) || !isClear(x, y, z + direction[1]))) continue;

            final var up = standingHeight(nx, y + 1, nz);
            if (up != null && up - base <= MAX_STEP_UP && isClear(x, y + height, z)) {
                addIfPassable(neighbors, nx, y + 1, nz);
            }
            // on land, a node above means the block at this level is solid, in water both can be valid
            final var maxFall = swimming != Swimming.SINK ? MAX_WATER_FALL : MAX_FALL;
            for (var dy = 0; dy <= maxFall; dy++) {
                if (dy > 0 && !isClear(nx, y - dy + 1, nz)) break;
                final var standing = standingHeight(nx, y - dy, nz);
                // along the wall it climbs
                if (standing == null && dy == 0 && isOnWall(nx, y, nz)) {
                    addIfPassable(neighbors, nx, y, nz);
                    break;
                }
                // onto a ladder, vine or scaffolding, to climb down it
                if (standing == null && isOnLadder(nx, y - dy, nz)) {
                    addIfPassable(neighbors, nx, y - dy, nz);
                    break;
                }
                if (standing == null) continue;
                // deeper drops only into water
                if (dy > MAX_FALL && !isWater(world.getBlockAt(nx, y - dy, nz))) break;
                if (standing - base <= MAX_STEP_UP) addIfPassable(neighbors, nx, y - dy, nz);
                break;
            }
        }
        return neighbors;
    }

    // all 26 directions, without cutting corners
    private List<Long> flyingNeighbors(final long node) {
        final var x = x(node);
        final var y = y(node);
        final var z = z(node);
        final var neighbors = new ArrayList<Long>(26);
        for (var dx = -1; dx <= 1; dx++) {
            for (var dy = -1; dy <= 1; dy++) {
                for (var dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (dx != 0 && !isClear(x + dx, y, z)) continue;
                    if (dy != 0 && !isClear(x, y + dy, z)) continue;
                    if (dz != 0 && !isClear(x, y, z + dz)) continue;
                    addIfPassable(neighbors, x + dx, y + dy, z + dz);
                }
            }
        }
        return neighbors;
    }

    private void addIfPassable(final List<Long> neighbors, final int x, final int y, final int z) {
        final var node = node(x, y, z);
        if (nodeHeight(x, y, z) != null && malus(node) != BLOCKED) neighbors.add(node);
    }

    // the height of the entity in the node, standing, swimming or climbing
    private @Nullable Double nodeHeight(final int x, final int y, final int z) {
        final var standing = standingHeight(x, y, z);
        if (standing != null) return standing;
        return isOnWall(x, y, z) || isOnLadder(x, y, z) ? (double) y : null;
    }

    // whether the entity is in a ladder, vine or scaffolding it can hold on to
    private boolean isOnLadder(final int x, final int y, final int z) {
        return isClear(x, y, z) && isClimbable(world.getBlockAt(x, y, z));
    }

    // a block next to a wall an entity that climbs walls can cling to
    private boolean isOnWall(final int x, final int y, final int z) {
        if (!climbs || !isClear(x, y, z)) return false;
        for (var i = 0; i < 4; i++) {
            final var direction = DIRECTIONS[i];
            if (!isAccessible(x + direction[0], z + direction[1])) continue;
            for (var h = 0; h < height; h++) {
                final var block = world.getBlockAt(x + direction[0], y + h, z + direction[1]);
                if (!block.isPassable() && !block.isLiquid()) return true;
            }
        }
        return false;
    }

    // the malus of the path type of the node, cached for the duration of the search
    private float malus(final long node) {
        return malus.computeIfAbsent(node, key -> pathMalus(x(key), y(key), z(key)));
    }

    private float pathMalus(final int x, final int y, final int z) {
        var malus = 0f;
        for (var i = 0; i < height; i++) {
            final var block = world.getBlockAt(x, y + i, z);
            final var type = block.getType();
            if (type == Material.LAVA || type == Material.POWDER_SNOW) return BLOCKED;
            if (isFire(type)) malus = Math.max(malus, DAMAGE_FIRE);
            else if (isDamaging(type)) malus = Math.max(malus, DAMAGE_OTHER);
            else if (isWater(block) && swimming != Swimming.DIVE) malus = Math.max(malus, WATER);
        }
        final var below = world.getBlockAt(x, y - 1, z).getType();
        if (below == Material.MAGMA_BLOCK) malus = Math.max(malus, DAMAGE_FIRE);
        else if (below == Material.HONEY_BLOCK) malus = Math.max(malus, STICKY_HONEY);
        if (malus > 0) return malus;
        return isNearDanger(x, y, z) ? DANGER : 0;
    }

    // like vanilla, blocks next to fire, lava and damaging blocks are dangerous
    private boolean isNearDanger(final int x, final int y, final int z) {
        for (var dx = -1; dx <= 1; dx++) {
            for (var dz = -1; dz <= 1; dz++) {
                if (!isAccessible(x + dx, z + dz)) continue;
                for (var dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    final var type = world.getBlockAt(x + dx, y + dy, z + dz).getType();
                    if (type == Material.LAVA || type == Material.MAGMA_BLOCK || isFire(type) || isDamaging(type))
                        return true;
                }
            }
        }
        return false;
    }

    private static boolean isFire(final Material type) {
        return type == Material.FIRE || type == Material.SOUL_FIRE
               || type == Material.CAMPFIRE || type == Material.SOUL_CAMPFIRE;
    }

    private static boolean isDamaging(final Material type) {
        return type == Material.SWEET_BERRY_BUSH || type == Material.WITHER_ROSE || type == Material.CACTUS;
    }

    // whether the entity fits into the column at the given block
    private boolean isClear(final int x, final int y, final int z) {
        if (!isAccessible(x, z) || y + height > world.getMaxHeight()) return false;
        for (var i = 0; i < height; i++) {
            if (!canOccupy(world.getBlockAt(x, y + i, z))) return false;
        }
        return true;
    }

    private boolean isAccessible(final int x, final int z) {
        final var chunkX = x >> 4;
        final var chunkZ = z >> 4;
        return world.isChunkLoaded(chunkX, chunkZ) && server.isOwnedByCurrentRegion(world, chunkX, chunkZ);
    }

    private List<Location> path(final Map<Long, Long> parents, final long end) {
        final var path = new ArrayList<Location>();
        var current = end;
        while (parents.containsKey(current)) {
            final var x = x(current);
            final var y = y(current);
            final var z = z(current);
            final var height = nodeHeight(x, y, z);
            path.add(new Location(world, x + 0.5, height != null ? height : y, z + 0.5));
            current = parents.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    private static int manhattan(final long from, final long to) {
        return Math.abs(x(from) - x(to)) + Math.abs(y(from) - y(to)) + Math.abs(z(from) - z(to));
    }

    // vanilla uses the euclidean distance for both the cost and the heuristic
    private static double distance(final long from, final long to) {
        final double dx = x(from) - x(to);
        final double dy = y(from) - y(to);
        final double dz = z(from) - z(to);
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // 26 bits x, 12 bits y, 26 bits z, like BlockPos
    private static long node(final int x, final int y, final int z) {
        return ((long) x & 0x3FFFFFF) << 38 | ((long) z & 0x3FFFFFF) << 12 | (long) y & 0xFFF;
    }

    private static int x(final long node) {
        return (int) (node >> 38);
    }

    private static int y(final long node) {
        return (int) (node << 52 >> 52);
    }

    private static int z(final long node) {
        return (int) (node << 26 >> 38);
    }

    private record Node(long key, double cost, double estimate) implements Comparable<Node> {
        @Override
        public int compareTo(final Node other) {
            return Double.compare(estimate, other.estimate);
        }
    }
}
