package net.thenextlvl.character.plugin.character.action;

import net.thenextlvl.character.action.ClickType;
import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;

@NullMarked
public enum ClickTypes {
    ANY_CLICK(EnumSet.allOf(ClickType.class)),
    ANY_LEFT_CLICK(EnumSet.of(ClickType.LEFT, ClickType.SHIFT_LEFT)),
    ANY_RIGHT_CLICK(EnumSet.of(ClickType.RIGHT, ClickType.SHIFT_RIGHT)),
    LEFT_CLICK(EnumSet.of(ClickType.LEFT)),
    RIGHT_CLICK(EnumSet.of(ClickType.RIGHT)),
    SHIFT_CLICK(EnumSet.of(ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT)),
    SHIFT_LEFT_CLICK(EnumSet.of(ClickType.SHIFT_LEFT)),
    SHIFT_RIGHT_CLICK(EnumSet.of(ClickType.SHIFT_RIGHT));

    private final EnumSet<ClickType> clickTypes;

    ClickTypes(EnumSet<ClickType> clickTypes) {
        this.clickTypes = clickTypes;
    }

    public EnumSet<ClickType> getClickTypes() {
        return clickTypes;
    }
}
