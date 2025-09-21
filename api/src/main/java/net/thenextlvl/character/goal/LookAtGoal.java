package net.thenextlvl.character.goal;

import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LookAtGoal extends Goal {
    @Nullable
    Entity getTargetingEntity();

    @Nullable
    Location getTargetingLocation();

    LookAnchor getLookAnchor();

    interface Builder extends Goal.Builder<LookAtGoal> {
        Builder lookAt(LookAnchor anchor);

        Builder targetEntity(Entity targetingEntity);

        Builder targetLocation(Location targetingLocation);
    }
}
