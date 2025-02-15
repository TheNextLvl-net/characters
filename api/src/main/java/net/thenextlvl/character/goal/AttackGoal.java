package net.thenextlvl.character.goal;

import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AttackGoal extends WalkGoal {
    Entity getTarget();

    double getAttackRange();

    interface Builder extends WalkGoal.Builder<AttackGoal, Builder> {
        Builder attackRange(double range);

        Builder target(Entity target);
    }
}
