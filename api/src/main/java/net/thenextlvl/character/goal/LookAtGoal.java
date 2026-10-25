package net.thenextlvl.character.goal;

import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/**
 * Represents a goal that looks at an entity or location.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface LookAtGoal extends Goal {
    /**
     * Gets the entity to look at.
     *
     * @return target entity
     * @since 0.1.0
     */
    @Nullable
    @Contract(pure = true)
    Entity getTargetingEntity();

    /**
     * Gets the location to look at.
     *
     * @return target location
     * @since 0.1.0
     */
    @Nullable
    @Contract(pure = true)
    Location getTargetingLocation();

    /**
     * Gets the anchor used to look at the target.
     *
     * @return look anchor
     * @since 0.1.0
     */
    @Contract(pure = true)
    LookAnchor getLookAnchor();

    /**
     * A builder for look at goals.
     *
     * @since 0.1.0
     */
    interface Builder extends Goal.Builder<LookAtGoal> {
        /**
         * Sets the anchor used to look at the target.
         *
         * @param anchor look anchor
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder lookAt(LookAnchor anchor);

        /**
         * Sets the entity to look at.
         *
         * @param targetingEntity target entity
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder targetEntity(Entity targetingEntity);

        /**
         * Sets the location to look at.
         *
         * @param targetingLocation target location
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder targetLocation(Location targetingLocation);
    }
}
