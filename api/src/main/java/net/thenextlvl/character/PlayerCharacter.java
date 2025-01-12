package net.thenextlvl.character;

import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface PlayerCharacter extends Character<Player> {
    PlayerProfile getGameProfile();

    @Nullable
    ProfileProperty getTextures();

    SkinParts getSkinParts();

    UUID getUniqueId();

    boolean clearTextures();

    boolean isListed();

    boolean isRealPlayer();

    boolean setTextures(String value, @Nullable String signature);

    void setListed(boolean listed);

    void setRealPlayer(boolean real);

    void setSkinParts(SkinParts builder);
}
