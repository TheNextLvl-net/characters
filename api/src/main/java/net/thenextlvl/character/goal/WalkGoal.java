package net.thenextlvl.character.goal;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface WalkGoal extends Goal, PathfindOptions {

    interface Builder<T extends WalkGoal, B extends Builder<T, B>> extends Goal.Builder<T> {
        B avoidWater(boolean avoidWater);

        B canFloat(boolean canFloat);

        // S canFly(boolean canFly);

        B canOpenDoors(boolean canOpenDoors);

        B canPassDoors(boolean canPassDoors);

        B distanceMargin(double margin);

        B maxFallDistance(double distance);

        B speed(double speed);

        B speedMultiplier(double multiplier);

        B getSelf();
    }
}
