package net.thenextlvl.character.goal;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a goal that involves walking.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface WalkGoal extends Goal, PathfindOptions {
    /**
     * A builder for walk goals.
     *
     * @param <T> the goal type
     * @param <B> the builder type
     * @since 0.1.0
     */
    interface Builder<T extends WalkGoal, B extends Builder<T, B>> extends Goal.Builder<T> {
        /**
         * Sets whether the character avoids water.
         *
         * @param avoidWater whether to avoid water
         * @return this builder
         * @see PathfindOptions#isAvoidingWater()
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        B avoidWater(boolean avoidWater);

        /**
         * Sets whether the character can float.
         *
         * @param canFloat whether the character can float
         * @return this builder
         * @see PathfindOptions#canFloat()
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        B canFloat(boolean canFloat);

        /**
         * Sets whether the character can open doors.
         *
         * @param canOpenDoors whether the character can open doors
         * @return this builder
         * @see PathfindOptions#canOpenDoors()
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        B canOpenDoors(boolean canOpenDoors);

        /**
         * Sets whether the character can pass doors.
         *
         * @param canPassDoors whether the character can pass doors
         * @return this builder
         * @see PathfindOptions#canPassDoors()
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        B canPassDoors(boolean canPassDoors);

        /**
         * Sets the distance margin.
         *
         * @param margin distance margin
         * @return this builder
         * @see PathfindOptions#getDistanceMargin()
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        B distanceMargin(double margin);

        /**
         * Sets the max fall distance.
         *
         * @param distance max fall distance
         * @return this builder
         * @see PathfindOptions#getMaxFallDistance()
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        B maxFallDistance(double distance);

        /**
         * Sets the speed.
         *
         * @param speed speed
         * @return this builder
         * @see PathfindOptions#getSpeed()
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        B speed(double speed);

        /**
         * Sets the speed multiplier.
         *
         * @param multiplier speed multiplier
         * @return this builder
         * @see PathfindOptions#getSpeedMultiplier()
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        B speedMultiplier(double multiplier);

        /**
         * Returns this builder.
         *
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = " -> this", pure = true)
        B getSelf();
    }
}
