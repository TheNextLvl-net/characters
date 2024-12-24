package net.thenextlvl.character;

import net.thenextlvl.character.skin.Skin;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PlayerCharacter extends Character<Player> {
    Skin getSkin();

    boolean isTablistEntryHidden();

    void setTablistEntryHidden(boolean hidden);
}
