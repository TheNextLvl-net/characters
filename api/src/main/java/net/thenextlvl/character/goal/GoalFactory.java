package net.thenextlvl.character.goal;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * Factory for creating goals.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface GoalFactory {
    /**
     * Creates a new builder for a goal that attacks the given entity.
     *
     * @param entity the entity to attack
     * @return new attack goal builder
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    AttackGoal.Builder attack(Entity entity);

    /**
     * Creates a new builder for a goal that escapes from the given entity.
     *
     * @param entity the entity to escape from
     * @return new escape goal builder
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    EscapeGoal.Builder escape(Entity entity);

    /**
     * Creates a new builder for a goal that follows the given entity.
     *
     * @param entity the entity to follow
     * @return new follow entity goal builder
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    FollowEntityGoal.Builder follow(Entity entity);

    /**
     * Creates a new builder for a goal that follows the given path.
     *
     * @param path the path to follow
     * @return new follow path goal builder
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    FollowPathGoal.Builder follow(List<Location> path);

    /**
     * Creates a new builder for a goal that looks at the given entity.
     *
     * @param entity the entity to look at
     * @return new look at goal builder
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    LookAtGoal.Builder lookAt(Entity entity);

    /**
     * Creates a new builder for a goal that looks at the given location.
     *
     * @param location the location to look at
     * @return new look at goal builder
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    LookAtGoal.Builder lookAt(Location location);

    /**
     * Creates a new builder for a goal that walks to the given location.
     *
     * @param location the location to walk to
     * @return new walk to goal builder
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    WalkToGoal.Builder walkTo(Location location);
}
