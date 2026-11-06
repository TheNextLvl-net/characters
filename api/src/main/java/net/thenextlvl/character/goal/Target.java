package net.thenextlvl.character.goal;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

/**
 * The target of a goal.
 * <p>
 * Every viewer of a character has their own entity, so targets are resolved separately for every entity.
 * This allows a character to either look at or follow its viewer,
 * which is different for every player, or one specific player, which is the same for everybody.
 *
 * @since 0.9.0
 */
public sealed interface Target permits Target.Viewer, Target.OfPlayer, Target.NearestPlayer, Target.Position {
    /**
     * The viewer of the entity, every player sees the character target themselves.
     *
     * @return the viewer target
     * @since 0.9.0
     */
    @Contract(pure = true)
    static Target viewer() {
        return new Viewer();
    }

    /**
     * One specific player, every player sees the character target the same player.
     *
     * @param player the uuid of the player
     * @return the player target
     * @since 0.9.0
     */
    @Contract(value = "_ -> new", pure = true)
    static Target player(final UUID player) {
        return new OfPlayer(player);
    }

    /**
     * The player closest to the entity within the given radius.
     * <p>
     * When following, it is the player closest to the character's location instead,
     * as the entity is always closest to the player it follows.
     * <p>
     * Only players the viewer can see are considered, including the viewer themselves,
     * so the target can be different for every viewer.
     *
     * @param radius the radius to search players in
     * @return the nearest player target
     * @throws IllegalArgumentException if the radius is not positive
     * @since 0.9.0
     */
    @Contract(value = "_ -> new", pure = true)
    static Target nearestPlayer(final double radius) throws IllegalArgumentException {
        return new NearestPlayer(radius);
    }

    /**
     * A fixed location.
     *
     * @param location the location
     * @return the location target
     * @throws IllegalArgumentException if the world of the location is {@code null}
     * @since 0.9.0
     */
    @Contract(value = "_ -> new", pure = true)
    static Target location(final Location location) throws IllegalArgumentException {
        return new Position(location);
    }

    /**
     * The viewer of the entity.
     *
     * @see #viewer()
     * @since 0.9.0
     */
    record Viewer() implements Target {
    }

    /**
     * One specific player.
     *
     * @param player the uuid of the player
     * @see #player(UUID)
     * @since 0.9.0
     */
    record OfPlayer(UUID player) implements Target {
    }

    /**
     * The player closest to the entity.
     *
     * @param radius the radius to search players in
     * @see #nearestPlayer(double)
     * @since 0.9.0
     */
    record NearestPlayer(double radius) implements Target {
        public NearestPlayer {
            Preconditions.checkArgument(radius > 0, "Radius must be positive");
        }
    }

    /**
     * A fixed location.
     *
     * @param location the location
     * @see #location(Location)
     * @since 0.9.0
     */
    record Position(Location location) implements Target {
        public Position {
            Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
            location = location.clone();
        }

        @Override
        public Location location() {
            return location.clone();
        }
    }
}
