package net.thenextlvl.character.goal;

/**
 * A goal that moves the character.
 *
 * @since 0.9.0
 */
public sealed interface MovementGoal extends Goal permits FollowGoal, PatrolGoal, WanderGoal {
    /**
     * The default speed modifier, the normal movement speed of the entity.
     *
     * @since 0.9.0
     */
    double DEFAULT_SPEED = 1;

    /**
     * Returns the speed modifier.
     * <p>
     * Like the speed modifier of vanilla goals, this is multiplied with the movement speed attribute of the entity,
     * so {@code 1} is the normal speed of the entity and {@code 0.5} half of it.
     *
     * @return the speed modifier
     * @since 0.9.0
     */
    double speed();
}
