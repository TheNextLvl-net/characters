package net.thenextlvl.character.plugin.character.goal;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.goal.AttackGoal;
import net.thenextlvl.character.goal.PathfindOptions;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperAttackGoal extends PaperWalkGoal implements AttackGoal {
    private final Entity target;
    private final double attackRange;

    public PaperAttackGoal(CharacterPlugin plugin, PathfindOptions options, Entity target, double attackRange) {
        super(plugin, options);
        this.target = target;
        this.attackRange = attackRange;
    }

    @Override
    public Entity getTarget() {
        return target;
    }

    @Override
    public double getAttackRange() {
        return attackRange;
    }

    @Override
    public void start() {
        // todo: perform goal start logic
    }

    public static class Builder extends PaperWalkGoal.Builder<AttackGoal, AttackGoal.Builder> implements AttackGoal.Builder {
        private @Nullable Entity target;
        private double attackRange = 2.0;

        public Builder(CharacterPlugin plugin) {
            super(plugin);
        }

        @Override
        public AttackGoal.Builder attackRange(double range) {
            this.attackRange = range;
            return this;
        }

        @Override
        public AttackGoal.Builder target(Entity target) {
            this.target = target;
            return this;
        }

        @Override
        public AttackGoal build() {
            Preconditions.checkState(target != null, "Target cannot be null");
            return new PaperAttackGoal(plugin, options.clone(), target, attackRange);
        }

        @Override
        public AttackGoal.Builder getSelf() {
            return this;
        }
    }
}
