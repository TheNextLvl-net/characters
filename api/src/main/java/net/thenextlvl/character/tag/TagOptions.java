package net.thenextlvl.character.tag;

import org.bukkit.entity.TextDisplay;

public interface TagOptions {
    TextDisplay.TextAlignment getAlignment();
    boolean setAlignment(TextDisplay.TextAlignment alignment);
}
