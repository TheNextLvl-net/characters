package net.thenextlvl.character;

import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface PlayerCharacter extends Character<Player> {
    @Contract(pure = true)
    PlayerProfile getGameProfile();

    @Nullable
    @Contract(pure = true)
    ProfileProperty getTextures();

    @Contract(pure = true)
    SkinParts getSkinParts();

    @Contract(pure = true)
    UUID getUniqueId();

    @Contract(mutates = "this")
    boolean clearTextures();

    @Contract(pure = true)
    boolean isListed();

    @Contract(pure = true)
    boolean isRealPlayer();

    @Contract(mutates = "this")
    boolean setListed(boolean listed);

    @Contract(mutates = "this")
    boolean setRealPlayer(boolean real);

    @Contract(mutates = "this")
    boolean setSkinParts(SkinParts builder);

    @Contract(mutates = "this")
    boolean setTextures(String value, @Nullable String signature);
}
