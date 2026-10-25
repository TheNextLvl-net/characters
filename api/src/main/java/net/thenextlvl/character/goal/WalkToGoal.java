package net.thenextlvl.character.goal;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a goal that walks to a location.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface WalkToGoal extends WalkGoal {
    /**
     * Gets the location to walk to.
     *
     * @return goal location
     * @since 0.1.0
     */
    @Contract(pure = true)
    Location getGoal();

    /**
     * A builder for walk to goals.
     *
     * @since 0.1.0
     */
    interface Builder extends WalkGoal.Builder<WalkToGoal, Builder> {
        /**
         * Sets the location to walk to.
         *
         * @param goal goal location
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder goal(Location goal);
    }
}
