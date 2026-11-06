package net.thenextlvl.character.goal;

/**
 * Makes the character look around now and then, like vanilla's {@code RandomLookAroundGoal}.
 * <p>
 * Every tick it is not running, the goal starts with a small chance,
 * then the character looks in a random horizontal direction for one to two seconds.
 *
 * @since 0.9.0
 */
public record LookAroundGoal() implements Goal {
}
