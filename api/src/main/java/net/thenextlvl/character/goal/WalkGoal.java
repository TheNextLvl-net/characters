package net.thenextlvl.character.goal;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface WalkGoal extends Goal, PathfindOptions {

    interface Builder<T extends WalkGoal, S extends Builder<T, S>> extends Goal.Builder<T> {
        S avoidWater(boolean avoidWater);

        S canFloat(boolean canFloat);

        // S canFly(boolean canFly);

        S canOpenDoors(boolean canOpenDoors);

        S canPassDoors(boolean canPassDoors);

        S distanceMargin(double margin);

        S maxFallDistance(double distance);

        S speed(double speed);

        S speedMultiplier(double multiplier);

        S getSelf();
    }
}
