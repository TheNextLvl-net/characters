package net.thenextlvl.character.tag;

import core.nbt.serialization.TagSerializable;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface TagOptions extends TagSerializable {
    Billboard getBillboard();

    @Nullable
    Brightness getBrightness();

    @Nullable
    Color getBackgroundColor();

    TextAlignment getAlignment();

    Vector3f getScale();

    boolean hasTextShadow();

    boolean isDefaultBackground();

    boolean isSeeThrough();

    boolean setAlignment(TextAlignment alignment);

    boolean setBackgroundColor(@Nullable Color color);

    boolean setBillboard(Billboard billboard);

    boolean setBrightness(@Nullable Brightness brightness);

    boolean setDefaultBackground(boolean enabled);

    boolean setLineWidth(int width);

    boolean setScale(Vector3f vector3f);

    boolean setSeeThrough(boolean seeThrough);

    boolean setShadowRadius(float radius);

    boolean setShadowStrength(float strength);

    boolean setTextOpacity(byte opacity);

    boolean setTextShadow(boolean enabled);

    byte getTextOpacity();

    float getShadowRadius();

    float getShadowStrength();

    int getLineWidth();
}
