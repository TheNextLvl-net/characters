package net.thenextlvl.character.goal;

import com.google.common.base.Preconditions;

/**
 * Makes the character follow a target.
 * <p>
 * The character only follows a target that is within range of the character's location
 * and stops once it is close enough.
 *
 * @param target   the target to follow
 * @param distance the distance to keep to the target
 * @param range    the maximum distance between the character's location and the target
 * @param speed    the speed modifier, see {@link MovementGoal#speed()}
 * @since 0.9.0
 */
public record FollowGoal(Target target, double distance, double range, double speed) implements MovementGoal {
    /**
     * The default distance to keep.
     *
     * @since 0.9.0
     */
    public static final double DEFAULT_DISTANCE = 2;

    /**
     * The default range.
     *
     * @since 0.9.0
     */
    public static final double DEFAULT_RANGE = 16;

    public FollowGoal {
        Preconditions.checkArgument(distance >= 0, "Distance cannot be negative");
        Preconditions.checkArgument(range > distance, "Range must be greater than the distance");
        Preconditions.checkArgument(speed > 0, "Speed must be positive");
    }

    /**
     * Makes the character follow a target with the default distance, range and speed.
     *
     * @param target the target to follow
     * @since 0.9.0
     */
    public FollowGoal(final Target target) {
        this(target, DEFAULT_DISTANCE, DEFAULT_RANGE, DEFAULT_SPEED);
    }
}
