package net.thenextlvl.character.action;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@NullMarked
public class ClickAction<T> {
    private final ActionType<T> actionType;
    private final Map<Player, Long> cooldowns = new WeakHashMap<>();
    private @Nullable String permission;
    private EnumSet<ClickType> clickTypes;
    private Duration cooldown;
    private T input;

    public ClickAction(ActionType<T> actionType, EnumSet<ClickType> clickTypes, T input) {
        this(actionType, clickTypes, input, Duration.ZERO, null);
    }

    public ClickAction(ActionType<T> actionType, EnumSet<ClickType> clickTypes, T input, Duration cooldown, @Nullable String permission) {
        this.actionType = actionType;
        this.clickTypes = clickTypes;
        this.cooldown = cooldown;
        this.input = input;
        this.permission = permission;
    }

    public boolean canInvoke(Player player) {
        return (permission == null || player.hasPermission(permission)) && !isOnCooldown(player);
    }

    public ActionType<T> getActionType() {
        return actionType;
    }

    public EnumSet<ClickType> getClickTypes() {
        return clickTypes;
    }

    public Duration getCooldown() {
        return cooldown;
    }

    public T getInput() {
        return input;
    }

    public @Nullable String getPermission() {
        return permission;
    }

    public boolean invoke(Player player) {
        if (!canInvoke(player)) return false;
        if (cooldown.isPositive()) cooldowns.put(player, System.currentTimeMillis());
        actionType.invoke(player, input);
        return true;
    }

    public boolean isOnCooldown(Player player) {
        return cooldown.isPositive() && cooldowns.computeIfPresent(player, (ignored, lastUsed) -> {
            if (System.currentTimeMillis() - cooldown.toMillis() > lastUsed) return null;
            return lastUsed;
        }) != null;
    }

    public boolean isSupportedClickType(ClickType type) {
        return clickTypes.contains(type);
    }

    public boolean resetCooldown(Player player) {
        return cooldowns.remove(player) != null;
    }

    public boolean setClickTypes(EnumSet<ClickType> clickTypes) {
        if (Objects.equals(this.clickTypes, clickTypes)) return false;
        this.clickTypes = clickTypes;
        return true;
    }

    public boolean setCooldown(Duration cooldown) {
        if (Objects.equals(this.cooldown, cooldown)) return false;
        this.cooldown = cooldown;
        return true;
    }

    public boolean setInput(T input) {
        if (Objects.equals(this.input, input)) return false;
        this.input = input;
        return true;
    }

    public boolean setPermission(@Nullable String permission) {
        if (Objects.equals(this.permission, permission)) return false;
        this.permission = permission;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission, clickTypes, cooldown, input, actionType, cooldowns);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClickAction<?> that = (ClickAction<?>) o;
        return Objects.equals(permission, that.permission)
               && Objects.deepEquals(clickTypes, that.clickTypes)
               && Objects.equals(cooldown, that.cooldown)
               && Objects.equals(input, that.input)
               && Objects.equals(actionType, that.actionType);
    }
}
