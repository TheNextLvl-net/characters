package net.thenextlvl.character.tag;

import core.nbt.serialization.TagSerializable;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface TagOptions extends TagSerializable {
    Billboard getBillboard();

    @Nullable
    Color getBackgroundColor();

    TextAlignment getAlignment();

    boolean setAlignment(TextAlignment alignment);

    boolean setBackgroundColor(@Nullable Color color);

    boolean setBillboard(Billboard billboard);
}
