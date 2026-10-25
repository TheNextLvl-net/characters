package net.thenextlvl.character.goal;

import net.thenextlvl.binder.StaticBinder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

/**
 * Represents a goal a character can pursue.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface Goal {
    /**
     * Gets the factory for creating goals.
     *
     * @return goal factory
     * @since 0.9.0
     */
    static @CheckReturnValue GoalFactory factory() {
        return StaticBinder.getInstance(GoalFactory.class.getClassLoader()).find(GoalFactory.class);
    }

    /**
     * Checks if this goal is currently running.
     *
     * @return {@code true} if this goal is running, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isRunning();

    /**
     * Starts this goal.
     *
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    void start();

    /**
     * Cancels this goal.
     *
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    void cancel();

    /**
     * A builder for goals.
     *
     * @param <T> the goal type
     * @since 0.1.0
     */
    interface Builder<T extends Goal> {
        /**
         * Builds the goal.
         *
         * @return new goal
         * @since 0.1.0
         */
        @Contract(value = " -> new", pure = true)
        T build();
    }
}
