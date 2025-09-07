package net.thenextlvl.character.tag;

import net.thenextlvl.nbt.serialization.TagSerializable;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.Contract;
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

    @Contract(mutates = "this")
    boolean setAlignment(TextAlignment alignment);

    @Contract(mutates = "this")
    boolean setBackgroundColor(@Nullable Color color);

    @Contract(mutates = "this")
    boolean setBillboard(Billboard billboard);

    @Contract(mutates = "this")
    boolean setBrightness(@Nullable Brightness brightness);

    @Contract(mutates = "this")
    boolean setDefaultBackground(boolean enabled);

    @Contract(mutates = "this")
    boolean setLeftRotation(Quaternionf rotation);

    @Contract(mutates = "this")
    boolean setLineWidth(int width);

    @Contract(mutates = "this")
    boolean setOffset(Vector3f offset);

    @Contract(mutates = "this")
    boolean setOffsetX(float offset);

    @Contract(mutates = "this")
    boolean setOffsetY(float offset);

    @Contract(mutates = "this")
    boolean setOffsetZ(float offset);

    @Contract(mutates = "this")
    boolean setRightRotation(Quaternionf rotation);

    @Contract(mutates = "this")
    boolean setScale(Vector3f scale);

    @Contract(mutates = "this")
    boolean setScale(float scale);

    @Contract(mutates = "this")
    boolean setSeeThrough(boolean seeThrough);

    @Contract(mutates = "this")
    boolean setTextOpacity(float opacity);

    @Contract(mutates = "this")
    boolean setTextShadow(boolean enabled);

    float getTextOpacity();

    int getLineWidth();
}
