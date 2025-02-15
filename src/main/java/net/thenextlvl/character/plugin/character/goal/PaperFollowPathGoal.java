package net.thenextlvl.character.plugin.character.goal;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.goal.FollowPathGoal;
import net.thenextlvl.character.goal.PathfindOptions;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.Location;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class PaperFollowPathGoal extends PaperWalkGoal implements FollowPathGoal {
    private final List<Location> paths;
    private int pathIndex = 0;

    public PaperFollowPathGoal(CharacterPlugin plugin, PathfindOptions options, List<Location> paths) {
        super(plugin, options);
        this.paths = paths;
    }

    @Override
    public void start() {
        // todo: perform goal start logic
    }

    @Override
    public @Unmodifiable List<Location> getPaths() {
        return paths.stream().map(Location::clone).toList();
    }

    @Override
    public @Nullable Location getCurrentPath() {
        return paths.get(pathIndex).clone();
    }

    @Override
    public @Nullable Location getGoal() {
        return paths.getLast().clone();
    }

    @Override
    public @Nullable Location getNextPath() {
        return pathIndex + 1 < paths.size() ? paths.get(pathIndex + 1).clone() : null;
    }

    public static class Builder extends PaperWalkGoal.Builder<FollowPathGoal, FollowPathGoal.Builder> implements FollowPathGoal.Builder {
        private @Nullable List<Location> paths;

        public Builder(CharacterPlugin plugin) {
            super(plugin);
        }

        @Override
        public FollowPathGoal.Builder paths(List<Location> paths) {
            this.paths = paths;
            return this;
        }

        @Override
        public FollowPathGoal build() {
            Preconditions.checkState(paths != null, "Paths cannot be null");
            Preconditions.checkState(!paths.isEmpty(), "Paths cannot be empty");
            return new PaperFollowPathGoal(plugin, options.clone(), paths.stream().map(Location::clone).toList());
        }

        @Override
        public FollowPathGoal.Builder getSelf() {
            return this;
        }
    }
}
