package net.thenextlvl.character.action;

/**
 * Represents the type of interaction performed by a player.
 *
 * @since 0.1.0
 */
public enum ClickType {
    /**
     * Represents a left-click interaction performed by a player.
     * <p>
     * This type of interaction occurs when a player attacks.
     *
     * @since 0.1.0
     */
    LEFT,

    /**
     * Represents a right-click interaction performed by a player.
     * <p>
     * This type of interaction occurs when a player interacts.
     *
     * @since 0.1.0
     */
    RIGHT,

    /**
     * Represents a shift-left-click interaction performed by a player.
     * <p>
     * This type of interaction occurs when a player attacks while sneaking.
     *
     * @since 0.1.0
     */
    SHIFT_LEFT,

    /**
     * Represents a shift-right-click interaction performed by a player.
     * <p>
     * This type of interaction occurs when a player interacts while sneaking.
     *
     * @since 0.1.0
     */
    SHIFT_RIGHT;

    /**
     * Checks if the interaction type is a left-click.
     *
     * @return whether this interaction represents a left-click
     * @since 0.1.0
     */
    public boolean isLeftClick() {
        return equals(LEFT) || equals(SHIFT_LEFT);
    }

    /**
     * Checks if the interaction type is a right-click.
     *
     * @return whether this interaction represents a right-click
     * @since 0.1.0
     */
    public boolean isRightClick() {
        return equals(RIGHT) || equals(SHIFT_RIGHT);
    }

    /**
     * Checks if the interaction type is a shift-click.
     *
     * @return whether this interaction represents either a shift-left-click or a shift-right-click
     * @since 0.1.0
     */
    public boolean isShiftClick() {
        return equals(SHIFT_LEFT) || equals(SHIFT_RIGHT);
    }
}
