package net.thenextlvl.character.plugin.character.action;

import net.thenextlvl.character.action.ClickType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum ClickTypes {
    ANY_CLICK(ClickType.values()),
    ANY_LEFT_CLICK(ClickType.LEFT, ClickType.SHIFT_LEFT),
    ANY_RIGHT_CLICK(ClickType.RIGHT, ClickType.SHIFT_RIGHT),
    LEFT_CLICK(ClickType.LEFT),
    RIGHT_CLICK(ClickType.RIGHT),
    SHIFT_CLICK(ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT),
    SHIFT_LEFT_CLICK(ClickType.SHIFT_LEFT),
    SHIFT_RIGHT_CLICK(ClickType.SHIFT_RIGHT);

    private final ClickType[] clickTypes;

    ClickTypes(ClickType... clickTypes) {
        this.clickTypes = clickTypes;
    }

    public ClickType[] getClickTypes() {
        return clickTypes;
    }
}
