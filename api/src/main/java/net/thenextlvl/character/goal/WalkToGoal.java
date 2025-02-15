package net.thenextlvl.character.goal;

import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface WalkToGoal extends WalkGoal {
    Location getGoal();

    interface Builder extends WalkGoal.Builder<WalkToGoal, Builder> {
        Builder goal(Location goal);
    }
}
