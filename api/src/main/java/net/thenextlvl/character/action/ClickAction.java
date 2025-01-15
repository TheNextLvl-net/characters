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
    private @Nullable String permission;
    private EnumSet<ClickType> clickTypes;
    private Duration cooldown;
    private T input;
    private final ActionType<T> actionType;

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

    private final Map<Player, Long> cooldowns = new WeakHashMap<>();

    public boolean isOnCooldown(Player player) {
        return cooldown.isPositive() && cooldowns.computeIfPresent(player, (ignored, lastUsed) -> {
            if (System.currentTimeMillis() - cooldown.toMillis() > lastUsed) return null;
            return lastUsed;
        }) != null;
    }

    public boolean resetCooldown(Player player) {
        return cooldowns.remove(player) != null;
    }

    public boolean canInvoke(Player player) {
        return (permission == null || player.hasPermission(permission)) && !isOnCooldown(player);
    }

    public boolean invoke(Player player) {
        if (!canInvoke(player)) return false;
        if (cooldown.isPositive()) cooldowns.put(player, System.currentTimeMillis());
        actionType.invoke(player, input);
        return true;
    }

    public boolean isSupportedClickType(ClickType type) {
        return clickTypes.contains(type);
    }

    public void setClickTypes(EnumSet<ClickType> clickTypes) {
        this.clickTypes = clickTypes;
    }

    public void setCooldown(Duration cooldown) {
        this.cooldown = cooldown;
    }

    public void setInput(T input) {
        this.input = input;
    }

    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }

    public @Nullable String getPermission() {
        return permission;
    }

    public ActionType<T> getActionType() {
        return actionType;
    }

    public Duration getCooldown() {
        return cooldown;
    }

    public EnumSet<ClickType> getClickTypes() {
        return clickTypes;
    }

    public T getInput() {
        return input;
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

    @Override
    public int hashCode() {
        return Objects.hash(permission, clickTypes, cooldown, input, actionType, cooldowns);
    }
}
