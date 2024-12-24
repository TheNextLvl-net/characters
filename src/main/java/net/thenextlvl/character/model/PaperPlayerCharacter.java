package net.thenextlvl.character.model;

import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.thenextlvl.character.CharacterPlugin;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.skin.Skin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class PaperPlayerCharacter extends PaperCharacter<Player> implements PlayerCharacter {
    private final PlayerProfile profile;
    private final Skin skin = new CharacterSkin();
    private boolean tablistEntryHidden;

    public PaperPlayerCharacter(CharacterPlugin plugin, String name) {
        super(plugin, name, EntityType.PLAYER);
        this.profile = plugin.getServer().createProfile(UUID.randomUUID(), name);
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

    @Override
    public boolean isTablistEntryHidden() {
        return tablistEntryHidden;
    }

    @Override
    public void setTablistEntryHidden(boolean hidden) {
        this.tablistEntryHidden = hidden;
    }

    @NullMarked
    public class CharacterSkin implements Skin {
        private SkinParts parts = new CharacterSkinPartBuilder().build();

        @Override
        public PlayerTextures getTextures() {
            return profile.getTextures();
        }

        @Override
        public SkinParts getParts() {
            return parts;
        }

        @Override
        public void setParts(SkinParts parts) {
            this.parts = parts;
        }

        @Override
        public void setTextures(PlayerTextures textures) {
            profile.setTextures(textures);
        }
    }
}
