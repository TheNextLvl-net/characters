package net.thenextlvl.character.plugin.character.goal;

import net.thenextlvl.character.goal.PathfindOptions;

public class PaperPathfindOptions implements PathfindOptions, Cloneable {
    private boolean avoidWater = true;
    private boolean canFloat = false;
    private boolean canOpenDoors = false;
    private boolean canPassDoors = true;
    private double distanceMargin = 0.5;
    private double maxFallDistance = 3.0;
    private double speed = 1.0;
    private double speedMultiplier = 1.0;

    @Override
    public boolean canFloat() {
        return canFloat;
    }

    @Override
    public boolean canOpenDoors() {
        return canOpenDoors;
    }

    @Override
    public boolean canPassDoors() {
        return canPassDoors;
    }

    @Override
    public boolean isAvoidingWater() {
        return avoidWater;
    }

    @Override
    public double getDistanceMargin() {
        return distanceMargin;
    }

    @Override
    public double getMaxFallDistance() {
        return maxFallDistance;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setAvoidWater(boolean avoidWater) {
        this.avoidWater = avoidWater;
    }

    public void setCanFloat(boolean canFloat) {
        this.canFloat = canFloat;
    }

    public void setCanOpenDoors(boolean canOpenDoors) {
        this.canOpenDoors = canOpenDoors;
    }

    public void setCanPassDoors(boolean canPassDoors) {
        this.canPassDoors = canPassDoors;
    }

    public void setDistanceMargin(double distanceMargin) {
        this.distanceMargin = distanceMargin;
    }

    public void setMaxFallDistance(double maxFallDistance) {
        this.maxFallDistance = maxFallDistance;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public PaperPathfindOptions clone() {
        try {
            return (PaperPathfindOptions) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
