package net.thenextlvl.character.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.EnumSet;
import java.util.function.Consumer;

@NullMarked
public sealed interface ClickAction<T> permits SimpleClickAction {
    @Contract(pure = true)
    ActionType<T> getActionType();

    @Contract(pure = true)
    EnumSet<ClickType> getClickTypes();

    @Contract(mutates = "this")
    boolean setClickTypes(EnumSet<ClickType> clickTypes);

    @Contract(pure = true)
    boolean isSupportedClickType(ClickType type);

    @Contract(pure = true)
    T getInput();

    @Contract(mutates = "this")
    boolean setInput(T input);

    @Contract(pure = true)
    @Range(from = 0, to = 100)
    int getChance();

    @Contract(mutates = "this")
    boolean setChance(@Range(from = 0, to = 100) int chance);

    @Nullable
    @Contract(pure = true)
    String getPermission();

    @Contract(mutates = "this")
    boolean setPermission(@Nullable String permission);

    @Contract(pure = true)
    Duration getCooldown();

    @Contract(mutates = "this")
    boolean setCooldown(Duration cooldown);

    @Contract(pure = true)
    boolean isOnCooldown(Player player);

    @Contract(mutates = "this")
    boolean resetCooldown(Player player);

    @Contract(pure = true)
    boolean canInvoke(Player player);

    boolean invoke(Player player, Entity character);

    /**
     * @since 0.5.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> ClickAction<T> create(ActionType<T> actionType, EnumSet<ClickType> clickTypes, T input) {
        return new SimpleClickAction<>(actionType, clickTypes, input);
    }

    /**
     * @since 0.5.0
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    static <T> ClickAction<T> create(ActionType<T> actionType, EnumSet<ClickType> clickTypes, T input, Consumer<ClickAction<T>> configurator) {
        var action = create(actionType, clickTypes, input);
        configurator.accept(action);
        return action;
    }
}
