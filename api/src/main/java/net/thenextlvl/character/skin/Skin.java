package net.thenextlvl.character.skin;

import com.destroystokyo.paper.SkinParts;
import org.bukkit.profile.PlayerTextures;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Skin {
    PlayerTextures getTextures();

    SkinParts getParts();

    void setParts(SkinParts parts);

    void setTextures(PlayerTextures textures);
}
