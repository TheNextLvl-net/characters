package net.thenextlvl.character;

import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public interface PlayerCharacter extends Character<Player> {
    PlayerProfile getGameProfile();

    SkinParts getSkinParts();

    UUID getUniqueId();

    boolean isListed();

    boolean isRealPlayer();

    void setListed(boolean listed);

    void setRealPlayer(boolean real);

    void setSkinParts(SkinParts builder);
}
