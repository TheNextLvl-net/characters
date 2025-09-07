package net.thenextlvl.character.event.player;

import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerClickCharacterEvent extends PlayerCharacterEvent implements Cancellable {
    private final ClickType type;
    private final Entity clickedEntity;
    private boolean cancelled;

    @ApiStatus.Internal
    public PlayerClickCharacterEvent(Character<?> character, Entity clickedEntity, Player player, ClickType type) {
        super(character, player);
        this.clickedEntity = clickedEntity;
        this.type = type;
    }

    /**
     * Retrieves the type of click performed during the event.
     *
     * @return the {@code ClickType} representing the type of click
     */
    @Contract(pure = true)
    public ClickType getType() {
        return type;
    }

    /**
     * Retrieves the entity clicked during the event.
     *
     * @return the {@code Entity} that was interacted with
     */
    @Contract(pure = true)
    public Entity getClickedEntity() {
        return clickedEntity;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
