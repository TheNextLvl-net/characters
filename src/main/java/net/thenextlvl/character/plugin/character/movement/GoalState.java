package net.thenextlvl.character.plugin.character.movement;

import net.thenextlvl.character.goal.Goal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Which goal runs and how far along it is.
 * <p>
 * Every entity has its own state, unless the character moves alike for all viewers:
 * then all entities share the state of the character, so they switch goals at the same time.
 * The first entity ticked in a tick leads and decides when a goal is done, the others follow.
 */
@NullMarked
public final class GoalState {
    @Nullable Goal running;
    // the index of the goal that ran last, the next one gets its turn first
    int lastGoal = -1;
    int lookTicks;
    // the direction a look around goal looks in, in radians
    double lookAroundAngle;
    int patrolIndex;
    Mode mode = Mode.NONE;
    private long tick = -1;

    /**
     * What the leading entity does.
     */
    enum Mode {
        // a goal runs
        RUNNING,
        // a movement goal is satisfied, stand still
        HOLD,
        // no goal applies
        NONE
    }

    // whether the caller leads this tick, which is the first one to claim it
    synchronized boolean lead(final long tick) {
        if (this.tick == tick) return false;
        this.tick = tick;
        return true;
    }
}
