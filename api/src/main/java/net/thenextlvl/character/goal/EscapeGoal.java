package net.thenextlvl.character.goal;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a goal that escapes from an entity.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface EscapeGoal extends WalkGoal {
    /**
     * Gets the entity to escape from.
     *
     * @return the entity to escape from
     * @since 0.1.0
     */
    @Contract(pure = true)
    Entity getRunningFrom();

    /**
     * A builder for escape goals.
     *
     * @since 0.1.0
     */
    interface Builder extends WalkGoal.Builder<EscapeGoal, Builder> {
        /**
         * Sets the entity to escape from.
         *
         * @param runningFrom the entity to escape from
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder runningFrom(Entity runningFrom);
    }
}
