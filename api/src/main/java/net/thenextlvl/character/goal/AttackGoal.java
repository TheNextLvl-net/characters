package net.thenextlvl.character.goal;

import com.google.common.base.Preconditions;

/**
 * Makes the character chase and attack a player, like vanilla's {@code MeleeAttackGoal}.
 * <p>
 * The character only chases a target that is within range of the character's location
 * and attacks it once it is in reach, every second.
 * Every viewer has their own entity of the character, so a player is only ever hurt by their own entity,
 * the entities of other viewers only swing at them.
 * Players in creative or spectator mode are ignored, like by vanilla mobs.
 *
 * @param target the player to attack, targets that are no player are never attacked
 * @param range  the maximum distance between the character's location and the target
 * @param speed  the speed modifier, see {@link MovementGoal#speed()}
 * @since 0.9.0
 */
public record AttackGoal(Target target, double range, double speed) implements MovementGoal {
    /**
     * The default range.
     *
     * @since 0.9.0
     */
    public static final double DEFAULT_RANGE = 16;

    public AttackGoal {
        Preconditions.checkArgument(range > 0, "Range must be positive");
        Preconditions.checkArgument(speed > 0, "Speed must be positive");
    }

    /**
     * Makes the character attack a player within the default range at the default speed.
     *
     * @param target the player to attack
     * @since 0.9.0
     */
    public AttackGoal(final Target target) {
        this(target, DEFAULT_RANGE, DEFAULT_SPEED);
    }
}
