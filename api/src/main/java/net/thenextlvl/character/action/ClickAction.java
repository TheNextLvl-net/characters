package net.thenextlvl.character.action;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

@NullMarked
public class ClickAction<T> {
    private @Nullable String permission;
    private ClickType[] clickTypes;
    private Duration cooldown;
    private T input;
    private final ActionType<T> actionType;

    public ClickAction(ActionType<T> actionType, ClickType[] clickTypes, T input) {
        this(actionType, clickTypes, input, Duration.ZERO, null);
    }

    public ClickAction(ActionType<T> actionType, ClickType[] clickTypes, T input, Duration cooldown, @Nullable String permission) {
        Preconditions.checkArgument(clickTypes.length > 0, "Click types cannot be empty");
        this.actionType = actionType;
        this.clickTypes = clickTypes;
        this.cooldown = cooldown;
        this.input = input;
        this.permission = permission;
    }

    public void invoke(Player player) {
        if (permission != null && !player.hasPermission(permission)) return;
        actionType.invoke(player, input);
    }

    public boolean isSupportedClickType(ClickType type) {
        for (var clickType : clickTypes) if (clickType.equals(type)) return true;
        return false;
    }

    public void setClickTypes(ClickType[] clickTypes) {
        Preconditions.checkArgument(clickTypes.length > 0, "Click types cannot be empty");
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

    public ClickType[] getClickTypes() {
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
        return Objects.hash(permission, Arrays.hashCode(clickTypes), cooldown, input, actionType);
    }
}
