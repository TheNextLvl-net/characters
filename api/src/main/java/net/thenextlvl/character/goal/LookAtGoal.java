package net.thenextlvl.character.goal;

import com.google.common.base.Preconditions;

/**
 * Makes the character look at a target while it is within range.
 *
 * @param target the target to look at
 * @param range  the maximum distance between the entity and the target
 * @since 0.9.0
 */
public record LookAtGoal(Target target, double range) implements Goal {
    /**
     * The default range.
     *
     * @since 0.9.0
     */
    public static final double DEFAULT_RANGE = 8;

    public LookAtGoal {
        Preconditions.checkArgument(range > 0, "Range must be positive");
    }

    /**
     * Makes the character look at a target within {@link #DEFAULT_RANGE}.
     *
     * @param target the target to look at
     * @since 0.9.0
     */
    public LookAtGoal(final Target target) {
        this(target, DEFAULT_RANGE);
    }
}
