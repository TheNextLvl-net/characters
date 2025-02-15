package net.thenextlvl.character.plugin.character.goal;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.goal.PathfindOptions;
import net.thenextlvl.character.goal.WalkToGoal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperWalkToGoal extends PaperWalkGoal implements WalkToGoal {
    private final Location goal;

    private PaperWalkToGoal(CharacterPlugin plugin, PathfindOptions options, Location goal) {
        super(plugin, options);
        this.goal = goal;
    }

    @Override
    public Location getGoal() {
        return goal;
    }

    @Override
    public void start() {
        // todo: perform goal start logic
    }

    public static class Builder extends PaperWalkGoal.Builder<WalkToGoal, WalkToGoal.Builder> implements WalkToGoal.Builder {
        private @Nullable Location goal;

        public Builder(CharacterPlugin plugin) {
            super(plugin);
        }

        @Override
        public WalkToGoal.Builder goal(Location goal) {
            this.goal = goal;
            return this;
        }

        @Override
        public WalkToGoal build() {
            Preconditions.checkState(goal != null, "Goal cannot be null");
            return new PaperWalkToGoal(plugin, options.clone(), goal.clone());
        }

        @Override
        public WalkToGoal.Builder getSelf() {
            return this;
        }
    }
}
