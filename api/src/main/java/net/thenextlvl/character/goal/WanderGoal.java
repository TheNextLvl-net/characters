package net.thenextlvl.character.goal;

import com.google.common.base.Preconditions;

/**
 * Makes the character walk to random locations around the character's location.
 * <p>
 * The locations are the same for every viewer.
 *
 * @param radius the maximum distance to the character's location
 * @param speed  the speed modifier, see {@link MovementGoal#speed()}
 * @since 0.9.0
 */
public record WanderGoal(double radius, double speed) implements MovementGoal {
    public WanderGoal {
        Preconditions.checkArgument(radius > 0, "Radius must be positive");
        Preconditions.checkArgument(speed > 0, "Speed must be positive");
    }

    /**
     * Makes the character wander within the given radius at the default speed.
     *
     * @param radius the maximum distance to the character's location
     * @since 0.9.0
     */
    public WanderGoal(final double radius) {
        this(radius, DEFAULT_SPEED);
    }
}
