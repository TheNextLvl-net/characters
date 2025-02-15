package net.thenextlvl.character.goal;

import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EscapeGoal extends WalkGoal {
    Entity getRunningFrom();

    interface Builder extends WalkGoal.Builder<EscapeGoal, Builder> {
        Builder runningFrom(Entity runningFrom);
    }
}
