package net.thenextlvl.character.action;

/**
 * Represents the type of interaction performed by a player.
 */
public enum ClickType {
    /**
     * Represents a left-click interaction performed by a player.
     * <p>
     * This type of interaction occurs when a player attacks.
     */
    LEFT,

    /**
     * Represents a right-click interaction performed by a player.
     * <p>
     * This type of interaction occurs when a player interacts.
     */
    RIGHT,

    /**
     * Represents a shift-left-click interaction performed by a player.
     * <p>
     * This type of interaction occurs when a player attacks while sneaking.
     */
    SHIFT_LEFT,

    /**
     * Represents a shift-right-click interaction performed by a player.
     * <p>
     * This type of interaction occurs when a player interacts while sneaking.
     */
    SHIFT_RIGHT;

    /**
     * Checks if the interaction type is a left-click.
     *
     * @return whether this interaction represents a left-click
     */
    public boolean isLeftClick() {
        return equals(LEFT) || equals(SHIFT_LEFT);
    }

    /**
     * Checks if the interaction type is a right-click.
     *
     * @return whether this interaction represents a right-click
     */
    public boolean isRightClick() {
        return equals(RIGHT) || equals(SHIFT_RIGHT);
    }

    /**
     * Checks if the interaction type is a shift-click.
     *
     * @return whether this interaction represents either a shift-left-click or a shift-right-click
     */
    public boolean isShiftClick() {
        return equals(SHIFT_LEFT) || equals(SHIFT_RIGHT);
    }
}
