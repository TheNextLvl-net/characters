package net.thenextlvl.character.plugin.character.goal;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.goal.EscapeGoal;
import net.thenextlvl.character.goal.PathfindOptions;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperEscapeGoal extends PaperWalkGoal implements EscapeGoal {
    private final Entity runningFrom;

    private PaperEscapeGoal(CharacterPlugin plugin, PathfindOptions options, Entity runningFrom) {
        super(plugin, options);
        this.runningFrom = runningFrom;
    }

    @Override
    public Entity getRunningFrom() {
        return runningFrom;
    }

    @Override
    public void start() {
        // todo: perform goal start logic
    }

    public static class Builder extends PaperWalkGoal.Builder<EscapeGoal, EscapeGoal.Builder> implements EscapeGoal.Builder {
        private @Nullable Entity runningFrom;

        public Builder(CharacterPlugin plugin) {
            super(plugin);
        }

        @Override
        public EscapeGoal.Builder runningFrom(Entity runningFrom) {
            this.runningFrom = runningFrom;
            return this;
        }

        @Override
        public EscapeGoal build() {
            Preconditions.checkState(runningFrom != null, "Entity cannot be null");
            return new PaperEscapeGoal(plugin, options.clone(), runningFrom);
        }

        @Override
        public EscapeGoal.Builder getSelf() {
            return this;
        }
    }
}
