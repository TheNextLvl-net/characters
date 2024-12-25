package net.thenextlvl.character.event.player;

import net.thenextlvl.character.Character;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerInteractCharacterEvent extends PlayerCharacterEvent implements Cancellable {
    private final InteractionType type;
    private boolean cancelled;

    public PlayerInteractCharacterEvent(Character<?> character, Player player, InteractionType type) {
        super(character, player);
        this.type = type;
    }

    /**
     * Retrieves the type of interaction performed during the event.
     *
     * @return the {@code InteractionType} representing the type of interaction
     */
    public InteractionType getType() {
        return type;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Represents the type of interaction performed by a player.
     */
    public enum InteractionType {
        /**
         * Represents a left-click interaction performed by a player.
         * <p>
         * This type of interaction occurs when a player attacks.
         */
        LEFT_CLICK,

        /**
         * Represents a right-click interaction performed by a player.
         * <p>
         * This type of interaction occurs when a player interacts.
         */
        RIGHT_CLICK,

        /**
         * Represents a shift-left-click interaction performed by a player.
         * <p>
         * This type of interaction occurs when a player attacks while sneaking.
         */
        SHIFT_LEFT_CLICK,

        /**
         * Represents a shift-right-click interaction performed by a player.
         * <p>
         * This type of interaction occurs when a player interacts while sneaking.
         */
        SHIFT_RIGHT_CLICK;

        /**
         * Checks if the interaction type is a left-click.
         *
         * @return whether this interaction represents a left-click
         */
        public boolean isLeftClick() {
            return equals(LEFT_CLICK) || equals(SHIFT_LEFT_CLICK);
        }

        /**
         * Checks if the interaction type is a right-click.
         *
         * @return whether this interaction represents a right-click
         */
        public boolean isRightClick() {
            return equals(RIGHT_CLICK) || equals(SHIFT_RIGHT_CLICK);
        }

        /**
         * Checks if the interaction type is a shift-click.
         *
         * @return whether this interaction represents either a shift-left-click or a shift-right-click
         */
        public boolean isShiftClick() {
            return equals(SHIFT_LEFT_CLICK) || equals(SHIFT_RIGHT_CLICK);
        }
    }
}
