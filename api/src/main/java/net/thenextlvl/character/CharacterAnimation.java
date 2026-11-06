package net.thenextlvl.character;

import org.bukkit.entity.LivingEntity;

/**
 * An animation a character can play.
 *
 * @see Character#playAnimation(CharacterAnimation)
 * @since 0.9.0
 */
public enum CharacterAnimation {
    /**
     * Swings the main hand, only played by living entities.
     *
     * @since 0.9.0
     */
    SWING_MAIN_HAND,
    /**
     * Swings the off hand, only played by living entities.
     *
     * @since 0.9.0
     */
    SWING_OFF_HAND,
    /**
     * Flashes red and shakes like when hurt, only played by living entities.
     *
     * @since 0.9.0
     */
    HURT,
    /**
     * The particles of a critical hit.
     *
     * @since 0.9.0
     */
    CRITICAL_HIT,
    /**
     * The particles of a critical hit with an enchanted weapon.
     *
     * @since 0.9.0
     */
    MAGIC_CRITICAL_HIT;

    /**
     * Checks if the given entity type can play this animation.
     *
     * @param entityClass the entity type
     * @return {@code true} if the animation can be played
     * @since 0.9.0
     */
    public boolean isApplicable(final Class<?> entityClass) {
        return switch (this) {
            case SWING_MAIN_HAND, SWING_OFF_HAND, HURT -> LivingEntity.class.isAssignableFrom(entityClass);
            case CRITICAL_HIT, MAGIC_CRITICAL_HIT -> true;
        };
    }
}
