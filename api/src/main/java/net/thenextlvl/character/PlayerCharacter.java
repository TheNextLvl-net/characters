package net.thenextlvl.character;

import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PlayerCharacter extends Character<Player> {
    PlayerProfile getGameProfile();

    SkinParts getSkinParts();

    boolean isListed();

    boolean isRealPlayer();

    void setListed(boolean listed);

    void setRealPlayer(boolean real);

    void setSkinParts(SkinParts builder);
}
