package net.thenextlvl.character.plugin.character.goal;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.goal.FollowEntityGoal;
import net.thenextlvl.character.goal.PathfindOptions;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperFollowEntityGoal extends PaperWalkGoal implements FollowEntityGoal {
    private final Entity target;

    private PaperFollowEntityGoal(CharacterPlugin plugin, PathfindOptions options, Entity entity) {
        super(plugin, options);
        this.target = entity;
    }

    @Override
    public Entity getTarget() {
        return target;
    }

    @Override
    public void start() {
        // todo: perform goal start logic
    }

    public static class Builder extends PaperWalkGoal.Builder<FollowEntityGoal, FollowEntityGoal.Builder> implements FollowEntityGoal.Builder {
        private @Nullable Entity target;

        public Builder(CharacterPlugin plugin) {
            super(plugin);
        }

        @Override
        public FollowEntityGoal.Builder target(Entity target) {
            this.target = target;
            return this;
        }

        @Override
        public FollowEntityGoal build() {
            Preconditions.checkState(target != null, "Target cannot be null");
            return new PaperFollowEntityGoal(plugin, options.clone(), target);
        }

        @Override
        public FollowEntityGoal.Builder getSelf() {
            return this;
        }
    }
}
