package net.thenextlvl.character.plugin.character.goal;

import net.thenextlvl.character.goal.PathfindOptions;
import net.thenextlvl.character.goal.WalkGoal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class PaperWalkGoal extends PaperGoal implements WalkGoal {
    private final PathfindOptions options;

    public PaperWalkGoal(CharacterPlugin plugin, PathfindOptions options) {
        super(plugin);
        this.options = options;
    }

    @Override
    public boolean canFloat() {
        return options.canFloat();
    }

    @Override
    public boolean canOpenDoors() {
        return options.canOpenDoors();
    }

    @Override
    public boolean canPassDoors() {
        return options.canPassDoors();
    }

    @Override
    public boolean isAvoidingWater() {
        return options.isAvoidingWater();
    }

    @Override
    public double getDistanceMargin() {
        return options.getDistanceMargin();
    }

    @Override
    public double getMaxFallDistance() {
        return options.getMaxFallDistance();
    }

    @Override
    public double getSpeed() {
        return options.getSpeed();
    }

    @Override
    public double getSpeedMultiplier() {
        return options.getSpeedMultiplier();
    }

    public static abstract class Builder<T extends WalkGoal, S extends WalkGoal.Builder<T, S>> extends PaperGoal.Builder<T> implements WalkGoal.Builder<T, S> {
        protected final PaperPathfindOptions options = new PaperPathfindOptions();

        public Builder(CharacterPlugin plugin) {
            super(plugin);
        }

        @Override
        public S avoidWater(boolean avoidWater) {
            options.setAvoidWater(avoidWater);
            return getSelf();
        }

        @Override
        public S canFloat(boolean canFloat) {
            options.setCanFloat(canFloat);
            return getSelf();
        }

        @Override
        public S canOpenDoors(boolean canOpenDoors) {
            options.setCanOpenDoors(canOpenDoors);
            return getSelf();
        }

        @Override
        public S canPassDoors(boolean canPassDoors) {
            options.setCanPassDoors(canPassDoors);
            return getSelf();
        }

        @Override
        public S distanceMargin(double margin) {
            options.setDistanceMargin(margin);
            return getSelf();
        }

        @Override
        public S maxFallDistance(double distance) {
            options.setMaxFallDistance(distance);
            return getSelf();
        }

        @Override
        public S speed(double speed) {
            options.setSpeed(speed);
            return getSelf();
        }

        @Override
        public S speedMultiplier(double multiplier) {
            options.setSpeedMultiplier(multiplier);
            return getSelf();
        }
    }
}
