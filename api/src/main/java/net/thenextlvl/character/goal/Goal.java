package net.thenextlvl.character.goal;

/**
 * A goal of a character.
 * <p>
 * Goals are persisted with the character and pursued by every entity of the character on its own.
 * <p>
 * Only one goal runs at a time and goals take turns: once the running goal is done,
 * the next goal that applies after it in their order starts, wrapping around at the end,
 * so a goal that always applies, like looking at a nearby viewer, does not keep the others from running.
 * A running goal is never interrupted.
 * Following is done once the target is close enough, wandering once a location is reached,
 * patrolling once a point is reached and looking after a few seconds, like vanilla's {@code LookAtPlayerGoal}.
 * <p>
 * A movement goal that applies but has nothing to do, like following a target that is close enough
 * or wandering while resting, keeps the entity standing where it is.
 * A follow goal only starts again once the target is a block farther away than the distance it keeps.
 * <p>
 * Without an applicable goal, an entity that was moved away by a goal walks back to the character's location,
 * other entities are not moved at all, so a character with only look goals stays wherever it is.
 * Without a running look goal, an entity at the character's location restores the character's rotation.
 *
 * @see net.thenextlvl.character.Character#addGoal(Goal)
 * @since 0.9.0
 */
public sealed interface Goal permits LookAtGoal, MovementGoal {
}
