package net.thenextlvl.character.plugin.character.goal;

import net.thenextlvl.character.goal.AttackGoal;
import net.thenextlvl.character.goal.EscapeGoal;
import net.thenextlvl.character.goal.FollowEntityGoal;
import net.thenextlvl.character.goal.FollowPathGoal;
import net.thenextlvl.character.goal.GoalFactory;
import net.thenextlvl.character.goal.LookAtGoal;
import net.thenextlvl.character.goal.WalkToGoal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class PaperGoalFactory implements GoalFactory {
    private final CharacterPlugin plugin;

    public PaperGoalFactory(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public AttackGoal.Builder attack(Entity entity) {
        return new PaperAttackGoal.Builder(plugin).target(entity);
    }

    @Override
    public EscapeGoal.Builder escape(Entity entity) {
        return new PaperEscapeGoal.Builder(plugin).runningFrom(entity);
    }

    @Override
    public FollowEntityGoal.Builder follow(Entity entity) {
        return new PaperFollowEntityGoal.Builder(plugin).target(entity);
    }

    @Override
    public FollowPathGoal.Builder follow(List<Location> path) {
        return new PaperFollowPathGoal.Builder(plugin).paths(path);
    }

    @Override
    public LookAtGoal.Builder lookAt(Entity entity) {
        return new PaperLookAtGoal.Builder(plugin).targetEntity(entity);
    }

    @Override
    public LookAtGoal.Builder lookAt(Location location) {
        return new PaperLookAtGoal.Builder(plugin).targetLocation(location);
    }

    @Override
    public WalkToGoal.Builder walkTo(Location location) {
        return new PaperWalkToGoal.Builder(plugin).goal(location);
    }
}
