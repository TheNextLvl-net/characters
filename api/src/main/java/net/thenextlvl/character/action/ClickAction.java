package net.thenextlvl.character.action;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

@NullMarked
public class ClickAction<T> {
    private @Nullable String permission;
    private ClickType[] clickTypes;
    private T input;
    private final ActionType<T> actionType;
    private final String name;

    public ClickAction(String name, ActionType<T> actionType, ClickType[] clickTypes, T input) {
        this(name, actionType, clickTypes, input, null);
    }

    public ClickAction(String name, ActionType<T> actionType, ClickType[] clickTypes, T input, @Nullable String permission) {
        Preconditions.checkArgument(clickTypes.length > 0, "Click types cannot be empty");
        this.name = name;
        this.actionType = actionType;
        this.clickTypes = clickTypes;
        this.permission = permission;
        this.input = input;
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

    public ClickType[] getClickTypes() {
        return clickTypes;
    }

    public String getName() {
        return name;
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
               && Objects.equals(input, that.input)
               && Objects.equals(actionType, that.actionType)
               && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission, Arrays.hashCode(clickTypes), input, actionType, name);
    }
}
