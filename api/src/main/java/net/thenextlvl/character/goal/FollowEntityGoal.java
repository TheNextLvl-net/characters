package net.thenextlvl.character.goal;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a goal that follows an entity.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface FollowEntityGoal extends WalkGoal {
    /**
     * Gets the entity to follow.
     *
     * @return target entity
     * @since 0.1.0
     */
    @Contract(pure = true)
    Entity getTarget();

    /**
     * A builder for follow entity goals.
     *
     * @since 0.1.0
     */
    interface Builder extends WalkGoal.Builder<FollowEntityGoal, Builder> {
        /**
         * Sets the entity to follow.
         *
         * @param target target entity
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder target(Entity target);
    }
}
