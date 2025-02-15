package net.thenextlvl.character.plugin.character.goal;

import com.google.common.base.Preconditions;
import io.papermc.paper.entity.LookAnchor;
import net.thenextlvl.character.goal.LookAtGoal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperLookAtGoal extends PaperGoal implements LookAtGoal {
    private final @Nullable Entity targetEntity;
    private final @Nullable Location targetLocation;
    private final LookAnchor lookAnchor;

    private PaperLookAtGoal(CharacterPlugin plugin, @Nullable Entity targetEntity, @Nullable Location targetLocation, LookAnchor lookAnchor) {
        super(plugin);
        this.targetEntity = targetEntity;
        this.targetLocation = targetLocation;
        this.lookAnchor = lookAnchor;
    }

    @Override
    public void start() {
        // todo: perform goal start logic
    }

    @Override
    public @Nullable Entity getTargetingEntity() {
        return targetEntity;
    }

    @Override
    public @Nullable Location getTargetingLocation() {
        return targetLocation;
    }

    @Override
    public LookAnchor getLookAnchor() {
        return lookAnchor;
    }

    public static class Builder extends PaperGoal.Builder<LookAtGoal> implements LookAtGoal.Builder {
        private @Nullable Entity targetEntity;
        private @Nullable Location targetLocation;
        private LookAnchor lookAnchor = LookAnchor.EYES;

        public Builder(CharacterPlugin plugin) {
            super(plugin);
        }

        @Override
        public LookAtGoal.Builder targetEntity(Entity targetingEntity) {
            this.targetEntity = targetingEntity;
            return this;
        }

        @Override
        public LookAtGoal.Builder targetLocation(Location targetingLocation) {
            this.targetLocation = targetingLocation;
            return this;
        }

        @Override
        public LookAtGoal.Builder lookAt(LookAnchor anchor) {
            this.lookAnchor = anchor;
            return this;
        }

        @Override
        public LookAtGoal build() {
            Preconditions.checkState(targetEntity != null || targetLocation != null, "Target cannot be null");
            return new PaperLookAtGoal(plugin, targetEntity, targetLocation != null ? targetLocation.clone() : null, lookAnchor);
        }
    }
}
