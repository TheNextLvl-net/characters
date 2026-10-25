package net.thenextlvl.character.goal;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a goal that attacks an entity.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface AttackGoal extends WalkGoal {
    /**
     * Gets the entity to attack.
     *
     * @return target entity
     * @since 0.1.0
     */
    @Contract(pure = true)
    Entity getTarget();

    /**
     * Gets the range from which the target can be attacked.
     *
     * @return attack range
     * @since 0.1.0
     */
    @Contract(pure = true)
    double getAttackRange();

    /**
     * A builder for attack goals.
     *
     * @since 0.1.0
     */
    interface Builder extends WalkGoal.Builder<AttackGoal, Builder> {
        /**
         * Sets the attack range.
         *
         * @param range attack range
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder attackRange(double range);

        /**
         * Sets the entity to attack.
         *
         * @param target target entity
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder target(Entity target);
    }
}
