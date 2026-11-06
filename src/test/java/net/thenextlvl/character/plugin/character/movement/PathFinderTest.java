package net.thenextlvl.character.plugin.character.movement;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.util.BoundingBox;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
class PathFinderTest {
    private static final int FLOOR = 63;

    @Test
    void walksStraightOnFlatGround() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        final var path = find(world, 0, 0, 5, 0);
        assertEquals(5, path.size());
        assertEnd(path, 5, FLOOR + 1, 0);
        path.forEach(location -> assertEquals(FLOOR + 1, location.getY()));
    }

    @Test
    void walksAroundWalls() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -3; z <= 3; z++) world.wall(2, z);
        final var path = find(world, 0, 0, 5, 0);
        assertEnd(path, 5, FLOOR + 1, 0);
        assertTrue(path.stream().noneMatch(location -> location.getBlockX() == 2 && Math.abs(location.getBlockZ()) <= 3));
    }

    @Test
    void doesNotCutCorners() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        world.wall(1, 0);
        final var path = find(world, 0, 0, 1, 1);
        // going diagonally from 0,0 to 1,1 would clip the wall at 1,0
        assertTrue(path.size() > 1);
        assertEnd(path, 1, FLOOR + 1, 1);
    }

    @Test
    void stepsUpOneBlock() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var x = 3; x <= 10; x++) for (var z = -10; z <= 10; z++) world.blocks.put(key(x, FLOOR + 1, z), Kind.SOLID);
        final var path = find(world, 0, 0, 5, 0);
        assertEnd(path, 5, FLOOR + 2, 0);
    }

    @Test
    void doesNotClimbTwoBlocks() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -10; z <= 10; z++) {
            world.blocks.put(key(3, FLOOR + 1, z), Kind.SOLID);
            world.blocks.put(key(3, FLOOR + 2, z), Kind.SOLID);
        }
        final var path = find(world, 0, 0, 5, 0);
        // unreachable, the path ends next to the step
        assertFalse(path.isEmpty());
        assertEquals(2, path.getLast().getBlockX());
    }

    @Test
    void worksWithNegativeCoordinates() {
        final var world = new FakeWorld();
        world.floor(-1010, -990);
        final var path = find(world, -1000, -1000, -995, -1000);
        assertEnd(path, -995, FLOOR + 1, -1000);
    }

    @Test
    void swimsAcrossWater() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a pool wider than the entity can jump with walls around it, the only way is through
        for (var x = 1; x <= 4; x++) for (var z = -10; z <= 10; z++) {
            world.blocks.remove(key(x, FLOOR, z));
            world.blocks.put(key(x, FLOOR, z), Kind.WATER);
            world.blocks.put(key(x, FLOOR - 1, z), Kind.SOLID);
        }
        final var path = find(world, 0, 0, 6, 0);
        assertEnd(path, 6, FLOOR + 1, 0);
    }

    @Test
    void swimsAtTheSurfaceIfItCanNotBreatheUnderwater() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a three deep pool between the start and the goal
        for (var x = 1; x <= 4; x++) for (var z = -10; z <= 10; z++) {
            for (var y = FLOOR - 2; y <= FLOOR; y++) world.blocks.put(key(x, y, z), Kind.WATER);
            world.blocks.put(key(x, FLOOR - 3, z), Kind.SOLID);
        }
        final var drowning = finder(world, false, PathFinder.Swimming.SURFACE).find(
                new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 6.5, FLOOR + 1, 0.5));
        assertEnd(drowning, 6, FLOOR + 1, 0);
        assertTrue(drowning.stream().allMatch(location -> location.getY() >= FLOOR), "dived below the surface");

        // a fish may also dive, but does not have to, so only check it can reach the goal
        final var fish = finder(world, false, PathFinder.Swimming.DIVE).find(
                new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 6.5, FLOOR + 1, 0.5));
        assertEnd(fish, 6, FLOOR + 1, 0);
    }

    @Test
    void doesNotDiveToReachUnderwaterGoalIfItCanNotBreathe() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var x = 1; x <= 4; x++) for (var z = -10; z <= 10; z++) {
            for (var y = FLOOR - 2; y <= FLOOR; y++) world.blocks.put(key(x, y, z), Kind.WATER);
            world.blocks.put(key(x, FLOOR - 3, z), Kind.SOLID);
        }
        final var finder = finder(world, false, PathFinder.Swimming.SURFACE);
        assertNull(finder.standingHeight(2, FLOOR - 2, 0));
        assertEquals(FLOOR, finder.standingHeight(2, FLOOR, 0));
        assertEquals(FLOOR - 2, finder(world, false, PathFinder.Swimming.DIVE).standingHeight(2, FLOOR - 2, 0));
    }

    @Test
    void sinksIfItCanNotSwim() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var x = 1; x <= 4; x++) for (var z = -10; z <= 10; z++) {
            for (var y = FLOOR - 2; y <= FLOOR; y++) world.blocks.put(key(x, y, z), Kind.WATER);
            world.blocks.put(key(x, FLOOR - 3, z), Kind.SOLID);
        }
        final var finder = finder(world, false, PathFinder.Swimming.SINK);
        // can not float at the surface, only stand on the ground below the water
        assertNull(finder.standingHeight(2, FLOOR, 0));
        assertEquals(FLOOR - 2, finder.standingHeight(2, FLOOR - 2, 0));
        // the pool is surrounded by land on both sides only, so it walks down and up again
        final var path = finder.find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 6.5, FLOOR + 1, 0.5));
        assertFalse(path.isEmpty());
        assertTrue(path.stream().noneMatch(location -> location.getBlockX() >= 1 && location.getBlockX() <= 4
                                                       && location.getY() >= FLOOR && location.getY() < FLOOR + 1));
    }

    @Test
    void prefersLandOverWater() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a small puddle in a straight line, walking around it is cheaper
        world.blocks.put(key(3, FLOOR + 1, 0), Kind.WATER);
        final var path = find(world, 0, 0, 6, 0);
        assertEnd(path, 6, FLOOR + 1, 0);
        assertTrue(path.stream().noneMatch(location -> location.getBlockX() == 3 && location.getBlockZ() == 0));
    }

    @Test
    void neverEntersLava() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -10; z <= 10; z++) world.blocks.put(key(3, FLOOR + 1, z), Kind.LAVA);
        final var path = find(world, 0, 0, 6, 0);
        assertTrue(path.stream().noneMatch(location -> location.getBlockX() == 3));
    }

    @Test
    void avoidsBlocksNextToDanger() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // lava right next to the straight line, vanilla marks the neighbouring blocks as dangerous
        for (var x = 1; x <= 5; x++) world.blocks.put(key(x, FLOOR, 1), Kind.LAVA);
        final var path = find(world, 0, 0, 6, 0);
        assertEnd(path, 6, FLOOR + 1, 0);
        assertTrue(path.stream().noneMatch(location -> location.getBlockZ() >= 0 && location.getBlockZ() <= 2
                                                       && location.getBlockX() >= 1 && location.getBlockX() <= 5),
                "walked next to the lava");
    }

    @Test
    void walksThroughDoors() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -10; z <= 10; z++) world.wall(3, z);
        world.blocks.put(key(3, FLOOR + 1, 0), Kind.DOOR);
        world.blocks.put(key(3, FLOOR + 2, 0), Kind.DOOR);
        final var path = find(world, 0, 0, 6, 0);
        assertEnd(path, 6, FLOOR + 1, 0);
        assertTrue(path.stream().anyMatch(location -> location.getBlockX() == 3 && location.getBlockZ() == 0));
    }

    @Test
    void doesNotOpenDoorsIfNotAllowed() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -10; z <= 10; z++) world.wall(3, z);
        world.blocks.put(key(3, FLOOR + 1, 0), Kind.DOOR);
        world.blocks.put(key(3, FLOOR + 2, 0), Kind.DOOR);
        final var path = find(world, 0, 0, 6, 0, false);
        assertTrue(path.stream().noneMatch(location -> location.getBlockX() >= 3));
    }

    @Test
    void walksThroughOpenDoorsIfNotAllowedToOpen() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -10; z <= 10; z++) world.wall(3, z);
        world.blocks.put(key(3, FLOOR + 1, 0), Kind.OPEN_DOOR);
        world.blocks.put(key(3, FLOOR + 2, 0), Kind.OPEN_DOOR);
        final var path = find(world, 0, 0, 6, 0, false);
        assertEnd(path, 6, FLOOR + 1, 0);
    }

    @Test
    void doesNotOpenIronDoors() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -10; z <= 10; z++) world.wall(3, z);
        world.blocks.put(key(3, FLOOR + 1, 0), Kind.IRON_DOOR);
        world.blocks.put(key(3, FLOOR + 2, 0), Kind.IRON_DOOR);
        final var path = find(world, 0, 0, 6, 0);
        assertTrue(path.stream().noneMatch(location -> location.getBlockX() >= 3));
    }

    @Test
    void walksAroundWallToTargetRightBehindIt() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a wall right between the start and the target, open one block to the side
        for (var z = -1; z <= 0; z++) world.wall(1, z);
        final var finder = finder(world, false);
        final var path = finder.find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 2.5, FLOOR + 1, 0.5), 0);
        assertTrue(path.reaches());
        assertEnd(path.nodes(), 2, FLOOR + 1, 0);
        assertTrue(path.nodes().stream().anyMatch(location -> location.getBlockZ() == 1), "did not walk around the wall");
    }

    @Test
    void reachesTargetsOnBlocksThatAreNoNodes() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // the target stands inside a block that is no node, like a player on a slab
        world.blocks.put(key(5, FLOOR + 1, 0), Kind.SOLID);
        final var finder = finder(world, false);
        final var target = new Location(world.proxy(), 5.5, FLOOR + 1.5, 0.5);
        assertFalse(finder.find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), target, 0).reaches());
        assertTrue(finder.find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), target, 1).reaches());
    }

    @Test
    void doesNotReachEnclosedTargets() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var x = 4; x <= 6; x++) for (var z = -1; z <= 1; z++) if (x != 5 || z != 0) world.wall(x, z);
        final var path = finder(world, false).find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 5.5, FLOOR + 1, 0.5), 0);
        assertFalse(path.reaches());
    }

    @Test
    void climbsWallsIfItCan() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a five block high wall across the whole floor, with the target on top of it
        for (var z = -10; z <= 10; z++) for (var y = FLOOR + 1; y <= FLOOR + 5; y++) world.blocks.put(key(3, y, z), Kind.SOLID);
        final var from = new Location(world.proxy(), 0.5, FLOOR + 1, 0.5);
        final var to = new Location(world.proxy(), 3.5, FLOOR + 6, 0.5);

        final var spider = new PathFinder(world.server(), world.proxy(), 0.9, 0.6, 0.65, false, PathFinder.Swimming.SURFACE, 48, true, false, false, Map.of());
        final var climbing = spider.find(from, to, 0);
        assertTrue(climbing.reaches(), "did not climb the wall");
        assertEnd(climbing.nodes(), 3, FLOOR + 6, 0);

        assertFalse(finder(world, false).find(from, to, 0).reaches(), "walked up a wall without climbing");

        // spiders are wider than a block, the wall is next to their footprint, not next to a single column
        final var wide = new PathFinder(world.server(), world.proxy(), 0.9, 1.4, 0.65, false, PathFinder.Swimming.SURFACE, 48, true, false, false, Map.of());
        final var wideClimbing = wide.find(new Location(world.proxy(), 0, FLOOR + 1, 0), new Location(world.proxy(), 4, FLOOR + 6, 0), 1);
        assertTrue(wideClimbing.reaches(), "the wide spider did not climb the wall: " + describe(wideClimbing.nodes()));
    }

    @Test
    void doesNotClimbWallsAboveIt() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a floating wall with a one block high gap below it, like vanilla, it is out of reach to climb from the ground
        for (var z = -10; z <= 10; z++) for (var x = 3; x <= 4; x++) for (var y = FLOOR + 2; y <= FLOOR + 5; y++) world.blocks.put(key(x, y, z), Kind.SOLID);
        final var spider = new PathFinder(world.server(), world.proxy(), 0.9, 1.4, 0.65, false, PathFinder.Swimming.SURFACE, 48, true, false, false, Map.of());
        final var path = spider.find(new Location(world.proxy(), 0, FLOOR + 1, 0), new Location(world.proxy(), 4, FLOOR + 6, 0), 1);
        assertFalse(path.reaches(), "climbed a wall it does not touch: " + describe(path.nodes()));
    }

    @Test
    void fliesOverWalls() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -10; z <= 10; z++) for (var y = FLOOR + 1; y <= FLOOR + 5; y++) world.blocks.put(key(3, y, z), Kind.SOLID);
        final var flyer = new PathFinder(world.server(), world.proxy(), 0.6, 0.6, 0.3, false, PathFinder.Swimming.SURFACE, 48, false, true, false, Map.of());
        final var path = flyer.find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 6.5, FLOOR + 1, 0.5), 0);
        assertTrue(path.reaches(), "did not fly over the wall");
        assertTrue(path.nodes().stream().anyMatch(location -> location.getY() > FLOOR + 5), "did not fly above the wall");
    }

    @Test
    void climbsLadders() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a five block high wall across the whole floor, with a ladder on it and the target on top
        for (var z = -10; z <= 10; z++) for (var y = FLOOR + 1; y <= FLOOR + 5; y++) world.blocks.put(key(3, y, z), Kind.SOLID);
        for (var y = FLOOR + 1; y <= FLOOR + 5; y++) world.blocks.put(key(2, y, 0), Kind.LADDER);
        final var path = finder(world, false).find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 3.5, FLOOR + 6, 0.5), 0);
        assertTrue(path.reaches(), "did not climb the ladder");
        assertTrue(path.nodes().stream().anyMatch(location -> location.getBlockX() == 2 && location.getBlockY() == FLOOR + 4), "did not use the ladder");
    }

    @Test
    void climbsDownLadders() {
        final var world = new FakeWorld();
        // solid ground from the shaft bottom up to the floor, with a one block shaft and a ladder on its wall
        for (var x = -10; x <= 10; x++) for (var z = -10; z <= 10; z++) for (var y = FLOOR - 6; y <= FLOOR; y++)
            world.blocks.put(key(x, y, z), Kind.SOLID);
        for (var y = FLOOR - 5; y <= FLOOR; y++) world.blocks.put(key(2, y, 0), Kind.LADDER);
        final var path = finder(world, false).find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 2.5, FLOOR - 5, 0.5), 0);
        assertTrue(path.reaches(), "did not climb down the ladder: " + path.nodes().stream()
                .map(location -> location.getX() + "/" + location.getY() + "/" + location.getZ()).toList());
    }

    // a five block high pillar, with a ladder on its east side and twisting vines on its north side, like in game
    private static FakeWorld pillar() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var y = FLOOR + 1; y <= FLOOR + 5; y++) {
            world.blocks.put(key(0, y, 0), Kind.SOLID);
            world.blocks.put(key(1, y, 0), Kind.LADDER);
            world.blocks.put(key(0, y, -1), Kind.VINE);
        }
        return world;
    }

    @Test
    void climbsDownLadderFromPillar() {
        final var world = pillar();
        final var path = finder(world, false).find(new Location(world.proxy(), 0.5, FLOOR + 6, 0.5), new Location(world.proxy(), 3.5, FLOOR + 1, 0.5), 0);
        assertTrue(path.reaches(), "did not climb down: " + describe(path.nodes()));
    }

    @Test
    void climbsUpLadderOntoPillar() {
        final var world = pillar();
        final var path = finder(world, false).find(new Location(world.proxy(), 2.5, FLOOR + 1, 0.5), new Location(world.proxy(), 0.5, FLOOR + 6, 0.5), 0);
        assertTrue(path.reaches(), "did not climb up: " + describe(path.nodes()));
        // straight up the ladder, then onto the pillar
        final var nodes = path.nodes();
        for (var i = 0; i < nodes.size() - 1; i++) {
            final var node = nodes.get(i);
            assertTrue(node.getBlockX() == 1 || node.getBlockX() == 2, "left the ladder early: " + describe(nodes));
        }
    }

    // the setup in game: a ladder on a one block wide ridge of dirt that leads up to the goal
    private static FakeWorld ridge() {
        final var world = new FakeWorld();
        // the ground, and the ridge at x 31 with its steps up along z
        for (var x = 25; x <= 40; x++) for (var z = 170; z <= 190; z++) world.blocks.put(key(x, 62, z), Kind.SOLID);
        for (final var block : new int[][]{
                {31, 63, 176}, {31, 63, 177}, {31, 63, 178}, {32, 63, 176}, {32, 63, 177}, {33, 63, 176},
                {31, 64, 177}, {31, 64, 178}, {31, 65, 177}, {31, 65, 178}, {31, 65, 179},
                {31, 66, 177}, {31, 66, 178}, {31, 66, 179}, {31, 66, 180},
                {30, 67, 177}, {31, 67, 177}, {31, 67, 178}, {31, 67, 179}, {31, 67, 180}, {31, 67, 181},
                {31, 68, 181}, {31, 68, 182}, {31, 69, 182}, {31, 69, 183}, {31, 70, 183}, {31, 71, 184},
                {31, 72, 185}, {32, 72, 185}, {33, 72, 185}, {34, 72, 185}, {35, 72, 185}, {31, 72, 186}, {31, 73, 186}
        }) world.blocks.put(key(block[0], block[1], block[2]), Kind.SOLID);
        for (var y = 64; y <= 67; y++) world.blocks.put(key(32, y, 177), Kind.LADDER);
        return world;
    }

    @Test
    void climbsLadderOntoRidge() {
        final var world = ridge();
        final var path = finder(world, false).find(new Location(world.proxy(), 32.5, 64, 177.5), new Location(world.proxy(), 35.5, 73, 185.5), 0);
        assertTrue(path.reaches(), "did not reach the goal: " + describe(path.nodes()));
        // off the top of the ladder straight onto the ridge, never onto the top of the ladder
        assertTrue(path.nodes().stream().noneMatch(location -> location.getBlockX() == 32 && location.getBlockZ() == 177
                                                               && location.getBlockY() >= 68), "stood on top of the ladder: " + describe(path.nodes()));
    }

    // the second setup in game: twisting vines and a ladder next to each other on a pillar, the target on top
    @Test
    void climbsVinesOntoPillar() {
        final var world = new FakeWorld();
        for (var x = 30; x <= 45; x++) for (var z = 168; z <= 180; z++) world.blocks.put(key(x, 63, z), Kind.SOLID);
        for (var y = 64; y <= 66; y++) world.blocks.put(key(39, y, 173), Kind.SOLID);
        for (var y = 64; y <= 71; y++) world.blocks.put(key(39, y, 174), Kind.SOLID);
        for (var x = 34; x <= 38; x++) world.blocks.put(key(x, 71, 174), Kind.SOLID);
        for (var x = 35; x <= 37; x++) world.blocks.put(key(x, 72, 174), Kind.SOLID);
        for (var y = 64; y <= 72; y++) world.blocks.put(key(40, y, 173), Kind.VINE);
        final var path = finder(world, false).find(new Location(world.proxy(), 41.5, 64, 173.5), new Location(world.proxy(), 37.6, 73, 174.5), 0);
        assertTrue(path.reaches(), "did not reach the top: " + describe(path.nodes()));
    }

    @Test
    void climbsDownVinesFromPillar() {
        final var world = pillar();
        final var path = finder(world, false).find(new Location(world.proxy(), 0.5, FLOOR + 6, 0.5), new Location(world.proxy(), 0.5, FLOOR + 1, -3.5), 0);
        assertTrue(path.reaches(), "did not climb down: " + describe(path.nodes()));
    }

    @Test
    void climbsUpVinesToPillar() {
        final var world = pillar();
        final var path = finder(world, false).find(new Location(world.proxy(), 0.5, FLOOR + 1, -3.5), new Location(world.proxy(), 0.5, FLOOR + 6, 0.5), 0);
        assertTrue(path.reaches(), "did not climb up: " + describe(path.nodes()));
    }

    private static List<String> describe(final List<Location> path) {
        return path.stream().map(location -> location.getX() + "/" + location.getY() + "/" + location.getZ()).toList();
    }

    // a ten block high cliff with a pool of water below its edge and a long way around
    private static FakeWorld cliff() {
        final var world = new FakeWorld();
        for (var x = -20; x <= 20; x++) for (var z = -20; z <= 20; z++) world.blocks.put(key(x, FLOOR - 10, z), Kind.SOLID);
        for (var x = -20; x <= 0; x++) for (var z = -20; z <= 20; z++) for (var y = FLOOR - 9; y <= FLOOR; y++)
            world.blocks.put(key(x, y, z), Kind.SOLID);
        // stairs down at the far end
        for (var step = 0; step < 10; step++) for (var x = 1; x <= 1 + step; x++) world.blocks.put(key(x, FLOOR - 1 - step, 20), Kind.SOLID);
        for (var x = 1; x <= 3; x++) for (var z = -1; z <= 1; z++) for (var y = FLOOR - 9; y <= FLOOR - 7; y++)
            world.blocks.put(key(x, y, z), Kind.WATER);
        return world;
    }

    @Test
    void dropsIntoWaterIfItCanSwim() {
        final var world = cliff();
        final var from = new Location(world.proxy(), -0.5, FLOOR + 1, 0.5);
        final var to = new Location(world.proxy(), 6.5, FLOOR - 9, 0.5);
        final var swimmer = finder(world, false, PathFinder.Swimming.SURFACE).find(from, to, 0);
        assertTrue(swimmer.reaches(), "did not reach the goal: " + describe(swimmer.nodes()));
        assertTrue(swimmer.nodes().stream().anyMatch(location -> location.getBlockX() >= 1 && location.getBlockX() <= 3
                                                                  && Math.abs(location.getBlockZ()) <= 1), "did not drop into the water: " + describe(swimmer.nodes()));

        final var sinker = finder(world, false, PathFinder.Swimming.SINK).find(from, to, 0);
        assertTrue(sinker.nodes().stream().noneMatch(location -> location.getBlockX() >= 1 && location.getBlockX() <= 3
                                                                 && Math.abs(location.getBlockZ()) <= 1 && location.getY() < FLOOR - 3),
                "dropped into the water without being able to swim");
    }

    @Test
    void jumpsOverGapsInsteadOfWalkingAround() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a one block wide trench across the floor, except for a bridge far away
        for (var z = -10; z <= 7; z++) world.blocks.remove(key(3, FLOOR, z));
        final var path = find(world, 0, 0, 6, 0);
        assertEnd(path, 6, FLOOR + 1, 0);
        assertTrue(path.stream().allMatch(location -> Math.abs(location.getBlockZ()) <= 1), "walked around the gap: " + describe(path));

        final var slow = new PathFinder(world.server(), world.proxy(), 1.8, 0.6, 1.62, false, PathFinder.Swimming.SURFACE, 48, false, false, false, Map.of())
                .find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 6.5, FLOOR + 1, 0.5));
        assertTrue(slow.stream().anyMatch(location -> location.getBlockZ() >= 8), "jumped without being fast enough: " + describe(slow));
    }

    @Test
    void jumpsOverLavaInsteadOfWalkingAround() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        for (var z = -10; z <= 7; z++) world.blocks.put(key(3, FLOOR, z), Kind.LAVA);
        final var path = find(world, 0, 0, 6, 0);
        assertEnd(path, 6, FLOOR + 1, 0);
        assertTrue(path.stream().allMatch(location -> Math.abs(location.getBlockZ()) <= 1), "walked around the lava: " + describe(path));
        assertTrue(path.stream().noneMatch(location -> location.getBlockX() == 3), "walked into the lava: " + describe(path));
    }

    @Test
    void stridersWalkOnLava() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a lava lake across the whole floor
        for (var x = 1; x <= 5; x++) for (var z = -10; z <= 10; z++) world.blocks.put(key(x, FLOOR, z), Kind.LAVA);
        final var strider = new PathFinder(world.server(), world.proxy(), 1.7, 0.9, 1.5, false, PathFinder.Swimming.SINK, 48, false, false, false,
                Map.of(PathFinder.PathType.LAVA, 0f, PathFinder.PathType.FIRE_IN_NEIGHBOR, 0f));
        final var path = strider.find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5), new Location(world.proxy(), 7.5, FLOOR + 1, 0.5), 0);
        assertTrue(path.reaches(), "did not walk on the lava: " + describe(path.nodes()));
        assertFalse(finder(world, false).find(new Location(world.proxy(), 0.5, FLOOR + 1, 0.5),
                new Location(world.proxy(), 7.5, FLOOR + 1, 0.5), 0).reaches(), "walked on lava without being a strider");
    }

    @Test
    void animalsDoNotWalkThroughFire() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        world.blocks.put(key(2, FLOOR + 1, 0), Kind.FIRE);
        final var finder = finder(world, false);
        // only a small malus for entities without overrides
        assertTrue(finder.find(new Location(world.proxy(), 2.5, FLOOR + 1, -1.5), new Location(world.proxy(), 2.5, FLOOR + 1, 0.5), 0).reaches());
        final var animal = new PathFinder(world.server(), world.proxy(), 1.4, 0.9, 1.3, false, PathFinder.Swimming.SURFACE, 48, false, false, false,
                Map.of(PathFinder.PathType.FIRE, -1f, PathFinder.PathType.FIRE_IN_NEIGHBOR, 16f));
        assertFalse(animal.find(new Location(world.proxy(), 2.5, FLOOR + 1, -1.5), new Location(world.proxy(), 2.5, FLOOR + 1, 0.5), 0).reaches(),
                "walked into fire");
    }

    @Test
    void wideEntitiesDoNotFitThroughNarrowGaps() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        // a wall across the floor with a one block wide gap, and a two block wide gap far away
        for (var z = -10; z <= 10; z++) if (z != 0 && z != 8 && z != 9) world.wall(3, z);
        final var from = new Location(world.proxy(), 0.5, FLOOR + 1, 0.5);
        final var to = new Location(world.proxy(), 6.5, FLOOR + 1, 0.5);
        final var narrow = finder(world, false).find(from, to, 0);
        assertTrue(narrow.reaches());
        assertTrue(narrow.nodes().stream().allMatch(location -> Math.abs(location.getBlockZ()) <= 2), "a narrow entity walked around: " + describe(narrow.nodes()));

        final var wide = new PathFinder(world.server(), world.proxy(), 1.6, 1.4, 1.4, false, PathFinder.Swimming.SURFACE, 48, false, false, false, Map.of())
                .find(new Location(world.proxy(), 1, FLOOR + 1, 1), new Location(world.proxy(), 7, FLOOR + 1, 1), 1);
        assertTrue(wide.reaches(), "the wide entity did not find the wide gap: " + describe(wide.nodes()));
        assertTrue(wide.nodes().stream().anyMatch(location -> location.getBlockZ() >= 8), "the wide entity squeezed through the narrow gap: " + describe(wide.nodes()));
    }

    @Test
    void returnsEmptyPathWhenAlreadyThere() {
        final var world = new FakeWorld();
        world.floor(-10, 10);
        assertTrue(find(world, 0, 0, 0, 0).isEmpty());
    }

    private static List<Location> find(final FakeWorld world, final int fromX, final int fromZ, final int toX, final int toZ) {
        return find(world, fromX, fromZ, toX, toZ, true);
    }

    private static List<Location> find(final FakeWorld world, final int fromX, final int fromZ, final int toX, final int toZ, final boolean openDoors) {
        final var finder = finder(world, openDoors);
        return finder.find(
                new Location(world.proxy(), fromX + 0.5, FLOOR + 1, fromZ + 0.5),
                new Location(world.proxy(), toX + 0.5, FLOOR + 1, toZ + 0.5)
        );
    }

    // a land entity like a mannequin
    private static PathFinder finder(final FakeWorld world, final boolean openDoors) {
        return finder(world, openDoors, PathFinder.Swimming.SURFACE);
    }

    private static PathFinder finder(final FakeWorld world, final boolean openDoors, final PathFinder.Swimming swimming) {
        // a larger follow range than the default, as the test worlds are mostly open floor
        return new PathFinder(world.server(), world.proxy(), 1.8, 0.6, 1.62, openDoors, swimming, 48, false, false, true, Map.of());
    }

    private static void assertEnd(final List<Location> path, final int x, final int y, final int z) {
        assertFalse(path.isEmpty(), "path is empty");
        final var end = path.getLast();
        assertEquals(x, end.getBlockX());
        assertEquals(y, end.getBlockY());
        assertEquals(z, end.getBlockZ());
    }

    private static long key(final int x, final int y, final int z) {
        return ((long) x & 0x3FFFFFF) << 38 | ((long) z & 0x3FFFFFF) << 12 | (long) y & 0xFFF;
    }

    private enum Kind {SOLID, WATER, LAVA, DOOR, OPEN_DOOR, IRON_DOOR, LADDER, VINE, FIRE}

    // a world of full solid blocks, liquids, doors and air, every chunk loaded and owned
    private static final class FakeWorld {
        private final Map<Long, Kind> blocks = new HashMap<>();
        private final World proxy = (World) Proxy.newProxyInstance(World.class.getClassLoader(), new Class<?>[]{World.class},
                (instance, method, args) -> switch (method.getName()) {
                    case "getBlockAt" -> block((int) args[0], (int) args[1], (int) args[2]);
                    case "isChunkLoaded" -> true;
                    case "getMinHeight" -> -64;
                    case "getMaxHeight" -> 320;
                    case "equals" -> instance == args[0];
                    case "hashCode" -> System.identityHashCode(instance);
                    default -> throw new UnsupportedOperationException(method.getName());
                });

        private void floor(final int from, final int to) {
            for (var x = from; x <= to; x++) for (var z = from; z <= to; z++) blocks.put(key(x, FLOOR, z), Kind.SOLID);
        }

        private void wall(final int x, final int z) {
            for (var y = FLOOR + 1; y <= FLOOR + 3; y++) blocks.put(key(x, y, z), Kind.SOLID);
        }

        private World proxy() {
            return proxy;
        }

        private Server server() {
            return (Server) Proxy.newProxyInstance(Server.class.getClassLoader(), new Class<?>[]{Server.class},
                    (instance, method, args) -> switch (method.getName()) {
                        case "isOwnedByCurrentRegion" -> true;
                        default -> throw new UnsupportedOperationException(method.getName());
                    });
        }

        private Block block(final int x, final int y, final int z) {
            final var kind = blocks.get(key(x, y, z));
            final var isDoor = kind == Kind.DOOR || kind == Kind.OPEN_DOOR || kind == Kind.IRON_DOOR;
            // open doors still have a thin collision box
            // ladders have a thin collision box
            final var collides = kind == Kind.SOLID || isDoor || kind == Kind.LADDER;
            final var door = isDoor ? Proxy.newProxyInstance(
                    Door.class.getClassLoader(), new Class<?>[]{Door.class}, (instance, method, args) -> switch (method.getName()) {
                        case "isOpen" -> kind == Kind.OPEN_DOOR;
                        default -> throw new UnsupportedOperationException(method.getName());
                    }) : null;
            return (Block) Proxy.newProxyInstance(Block.class.getClassLoader(), new Class<?>[]{Block.class},
                    (instance, method, args) -> switch (method.getName()) {
                        case "isPassable" -> !collides;
                        case "isLiquid" -> kind == Kind.WATER || kind == Kind.LAVA;
                        case "getType" -> kind == null ? Material.AIR : switch (kind) {
                            case SOLID -> Material.STONE;
                            case WATER -> Material.WATER;
                            case LAVA -> Material.LAVA;
                            case DOOR, OPEN_DOOR -> Material.OAK_DOOR;
                            case IRON_DOOR -> Material.IRON_DOOR;
                            case LADDER -> Material.LADDER;
                            case VINE -> Material.TWISTING_VINES_PLANT;
                            case FIRE -> Material.FIRE;
                        };
                        case "getBlockData" -> door;
                        case "getBoundingBox" -> collides ? new BoundingBox(x, y, z, x + 1, y + 1, z + 1) : new BoundingBox();
                        default -> throw new UnsupportedOperationException(method.getName());
                    });
        }
    }
}
