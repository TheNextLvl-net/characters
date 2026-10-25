package net.thenextlvl.character.goal;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Represents a goal that follows a path.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface FollowPathGoal extends WalkGoal {
    /**
     * Gets all locations of the path.
     *
     * @return path locations
     * @since 0.1.0
     */
    @Unmodifiable
    @Contract(pure = true)
    List<Location> getPaths();

    /**
     * Gets the location the character is currently walking to.
     *
     * @return current path location
     * @since 0.1.0
     */
    @Nullable
    @Contract(pure = true)
    Location getCurrentPath();

    /**
     * Gets the final location of the path.
     *
     * @return goal location
     * @since 0.1.0
     */
    @Nullable
    @Contract(pure = true)
    Location getGoal();

    /**
     * Gets the location the character will walk to next.
     *
     * @return next path location
     * @since 0.1.0
     */
    @Nullable
    @Contract(pure = true)
    Location getNextPath();

    /**
     * A builder for follow path goals.
     *
     * @since 0.1.0
     */
    interface Builder extends WalkGoal.Builder<FollowPathGoal, Builder> {
        /**
         * Sets the locations of the path.
         *
         * @param paths path locations
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder paths(List<Location> paths);
    }
}
