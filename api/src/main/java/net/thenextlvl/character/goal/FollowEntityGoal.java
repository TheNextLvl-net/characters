package net.thenextlvl.character.goal;

import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface FollowEntityGoal extends WalkGoal {
    Entity getTarget();

    interface Builder extends WalkGoal.Builder<FollowEntityGoal, Builder> {
        Builder target(Entity target);
    }
}
