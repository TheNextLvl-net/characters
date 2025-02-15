package net.thenextlvl.character.goal;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface GoalFactory {
    AttackGoal.Builder attack(Entity entity);

    EscapeGoal.Builder escape(Entity entity);

    FollowEntityGoal.Builder follow(Entity entity);

    FollowPathGoal.Builder follow(List<Location> path);

    LookAtGoal.Builder lookAt(Entity entity);

    LookAtGoal.Builder lookAt(Location location);

    WalkToGoal.Builder walkTo(Location location);
}
