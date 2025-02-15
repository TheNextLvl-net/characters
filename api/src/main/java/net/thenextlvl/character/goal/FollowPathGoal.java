package net.thenextlvl.character.goal;

import org.bukkit.Location;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface FollowPathGoal extends WalkGoal {
    @Unmodifiable
    List<Location> getPaths();

    @Nullable
    Location getCurrentPath();

    @Nullable
    Location getGoal();

    @Nullable
    Location getNextPath();

    interface Builder extends WalkGoal.Builder<FollowPathGoal, Builder> {
        Builder paths(List<Location> paths);
    }
}
