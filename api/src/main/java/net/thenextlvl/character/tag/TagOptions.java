package net.thenextlvl.character.tag;

import core.nbt.serialization.TagSerializable;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TagOptions extends TagSerializable {
    Billboard getBillboard();

    TextAlignment getAlignment();

    boolean setAlignment(TextAlignment alignment);

    boolean setBillboard(Billboard billboard);
}
