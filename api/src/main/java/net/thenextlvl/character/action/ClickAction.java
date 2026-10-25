package net.thenextlvl.character.action;

import net.thenextlvl.binder.StaticBinder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.Copyable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Optional;

/**
 * Represents a click action that can be performed on a character.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface ClickAction<T> extends Copyable<ClickAction<T>> {
    /**
     * Gets the factory for creating click actions.
     *
     * @return click action factory
     * @since 0.9.0
     */
    static @CheckReturnValue ClickActionFactory factory() {
        return StaticBinder.getInstance(ClickActionFactory.class.getClassLoader()).find(ClickActionFactory.class);
    }

    /**
     * Gets the action type of this click action.
     *
     * @return action type
     * @since 0.1.0
     */
    @Contract(pure = true)
    ActionType<T> getActionType();

    /**
     * Gets the click types of this click action.
     *
     * @return a clone of the click types
     * @since 0.1.0
     */
    @Contract(value = " -> new", pure = true)
    EnumSet<ClickType> getClickTypes();

    /**
     * Sets the click types of this click action.
     *
     * @param clickTypes new click types
     * @return {@code true} if the click types were successfully set, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setClickTypes(EnumSet<ClickType> clickTypes);

    /**
     * Checks if this click action supports the given click type.
     *
     * @param type click type to check
     * @return {@code true} if this click action supports the given click type, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isSupportedClickType(ClickType type);

    /**
     * Gets the input of this click action.
     *
     * @return input
     * @since 0.1.0
     */
    @Contract(pure = true)
    T getInput();

    /**
     * Sets the input of this click action.
     *
     * @param input new input
     * @return {@code true} if the input was successfully set, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setInput(T input);

    /**
     * Gets the chance of this click action in percent.
     *
     * @return chance in percent
     * @since 0.1.0
     */
    @Contract(pure = true)
    @Range(from = 0, to = 100)
    int getChance();

    /**
     * Sets the chance of this click action in percent.
     *
     * @param chance new chance in percent
     * @return {@code true} if the chance was successfully set, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setChance(@Range(from = 0, to = 100) int chance);

    /**
     * Gets the permission required to perform this click action.
     *
     * @return permission
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<String> getPermission();

    /**
     * Sets the permission required to perform this click action.
     *
     * @param permission new permission
     * @return {@code true} if the permission was successfully set, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setPermission(@Nullable String permission);

    /**
     * Gets the cooldown for this click action.
     *
     * @return cooldown
     * @since 0.1.0
     */
    @Contract(pure = true)
    Duration getCooldown();

    /**
     * Sets the cooldown for this click action.
     *
     * @param cooldown new cooldown
     * @return {@code true} if the cooldown was successfully set, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setCooldown(Duration cooldown);

    /**
     * Checks if this click action is on cooldown for the given player.
     *
     * @param player player to check
     * @return {@code true} if this click action is on cooldown for the given player, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isOnCooldown(Player player);

    /**
     * Resets the cooldown for the given player.
     *
     * @param player player to reset the cooldown for
     * @return {@code true} if the cooldown was successfully reset, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean resetCooldown(Player player);

    /**
     * Invokes this click action on the given character for the given player.
     *
     * @param character character
     * @param player    player
     * @return {@code true} if the action was successfully invoked, {@code false} otherwise
     * @since 0.9.0
     */
    boolean invoke(Character character, Player player);
}
