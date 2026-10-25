package net.thenextlvl.character.goal;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents the options used while pathfinding.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface PathfindOptions {
    /**
     * Checks if the character can float in water.
     *
     * @return {@code true} if the character can float, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean canFloat();

    /**
     * Checks if the character can open doors.
     *
     * @return {@code true} if the character can open doors, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean canOpenDoors();

    /**
     * Checks if the character can pass through open doors.
     *
     * @return {@code true} if the character can pass doors, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean canPassDoors();

    /**
     * Checks if the character avoids water.
     *
     * @return {@code true} if the character avoids water, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isAvoidingWater();

    /**
     * Gets the distance to the goal at which the path is marked as complete.
     *
     * @return distance margin
     * @since 0.1.0
     */
    @Contract(pure = true)
    double getDistanceMargin();

    /**
     * Gets the maximum distance the character may fall while pathfinding.
     *
     * @return max fall distance
     * @since 0.1.0
     */
    @Contract(pure = true)
    double getMaxFallDistance();

    /**
     * Gets the base speed at which the character is moving.
     *
     * @return speed
     * @since 0.1.0
     */
    @Contract(pure = true)
    double getSpeed();

    /**
     * Gets the multiplier for how much faster the character can move.
     *
     * @return speed multiplier
     * @since 0.1.0
     */
    @Contract(pure = true)
    double getSpeedMultiplier();
}
