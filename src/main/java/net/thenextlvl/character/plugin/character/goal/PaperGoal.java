package net.thenextlvl.character.plugin.character.goal;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class PaperGoal implements Goal {
    protected @Nullable ScheduledTask task;

    protected final CharacterPlugin plugin;

    public PaperGoal(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isRunning() {
        return task != null;
    }

    @Override
    public void cancel() {
        if (task != null) task.cancel();
        this.task = null;
    }

    public abstract static class Builder<T extends Goal> implements Goal.Builder<T> {
        protected final CharacterPlugin plugin;

        public Builder(CharacterPlugin plugin) {
            this.plugin = plugin;
        }

        public abstract T build();
    }
}
