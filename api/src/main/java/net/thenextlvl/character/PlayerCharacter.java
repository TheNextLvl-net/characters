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

    void setSkinParts(SkinParts builder);

    UUID getUniqueId();

    boolean clearTextures();

    boolean isListed();

    void setListed(boolean listed);

    boolean isRealPlayer();

    void setRealPlayer(boolean real);

    boolean setTextures(String value, @Nullable String signature);
}
