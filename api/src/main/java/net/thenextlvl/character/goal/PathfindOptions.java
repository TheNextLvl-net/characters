package net.thenextlvl.character.goal;

public interface PathfindOptions {
    boolean canFloat();

    boolean canOpenDoors();

    boolean canPassDoors();

    boolean isAvoidingWater();

    // at which distance to the goal the path is marked as complete
    double getDistanceMargin();

    // the max distance the character may fall while path finding
    double getMaxFallDistance();

    // the base speed at which the character is moving
    double getSpeed();

    // the speed multiplier how much faster the character can move
    double getSpeedMultiplier();
}
