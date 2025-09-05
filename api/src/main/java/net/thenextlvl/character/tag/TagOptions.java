package net.thenextlvl.character.tag;

import net.thenextlvl.nbt.serialization.TagSerializable;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.joml.Quaternionf;
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

    Quaternionf getLeftRotation();

    Quaternionf getRightRotation();

    TextAlignment getAlignment();

    Vector3f getOffset();

    Vector3f getScale();

    boolean hasTextShadow();

    boolean isDefaultBackground();

    boolean isSeeThrough();

    boolean setAlignment(TextAlignment alignment);

    boolean setBackgroundColor(@Nullable Color color);

    boolean setBillboard(Billboard billboard);

    boolean setBrightness(@Nullable Brightness brightness);

    boolean setDefaultBackground(boolean enabled);

    boolean setLeftRotation(Quaternionf rotation);

    boolean setLineWidth(int width);

    boolean setOffset(Vector3f offset);

    boolean setOffsetX(float offset);

    boolean setOffsetY(float offset);

    boolean setOffsetZ(float offset);

    boolean setRightRotation(Quaternionf rotation);

    boolean setScale(Vector3f scale);

    boolean setScale(float scale);

    boolean setSeeThrough(boolean seeThrough);

    boolean setTextOpacity(float opacity);

    boolean setTextShadow(boolean enabled);

    float getTextOpacity();

    int getLineWidth();
}
