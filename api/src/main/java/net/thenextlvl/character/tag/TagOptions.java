package net.thenextlvl.character.tag;

import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents the options of a character's name tag.
 *
 * @since 0.9.0
 */
@ApiStatus.NonExtendable
public interface TagOptions {
    /**
     * Gets the billboard of the name tag.
     *
     * @return billboard
     * @since 0.1.0
     */
    @Contract(pure = true)
    Billboard getBillboard();

    /**
     * Sets the billboard of the name tag.
     *
     * @param billboard new billboard
     * @return {@code true} if the billboard was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setBillboard(Billboard billboard);

    /**
     * Gets the brightness of the name tag.
     *
     * @return brightness
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Brightness> getBrightness();

    /**
     * Sets the brightness of the name tag.
     *
     * @param brightness new brightness
     * @return {@code true} if the brightness was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setBrightness(@Nullable Brightness brightness);

    /**
     * Gets the background color of the name tag.
     *
     * @return background color
     * @since 0.9.0
     */
    @Contract(pure = true)
    Optional<Color> getBackgroundColor();

    /**
     * Sets the background color of the name tag.
     *
     * @param color new background color
     * @return {@code true} if the background color was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setBackgroundColor(@Nullable Color color);

    /**
     * Checks if the name tag uses the default background.
     *
     * @return {@code true} if the default background is used, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isDefaultBackground();

    /**
     * Sets whether the name tag uses the default background.
     *
     * @param enabled whether to use the default background
     * @return {@code true} if the default background was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setDefaultBackground(boolean enabled);

    /**
     * Gets the text alignment of the name tag.
     *
     * @return text alignment
     * @since 0.1.0
     */
    @Contract(pure = true)
    TextAlignment getAlignment();

    /**
     * Sets the text alignment of the name tag.
     *
     * @param alignment new text alignment
     * @return {@code true} if the alignment was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setAlignment(TextAlignment alignment);

    /**
     * Gets the line width of the name tag.
     *
     * @return line width
     * @since 0.1.0
     */
    @Contract(pure = true)
    int getLineWidth();

    /**
     * Sets the line width of the name tag.
     *
     * @param width new line width
     * @return {@code true} if the line width was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setLineWidth(int width);

    /**
     * Gets the text opacity of the name tag in percent.
     *
     * @return text opacity
     * @since 0.1.0
     */
    @Contract(pure = true)
    float getTextOpacity();

    /**
     * Sets the text opacity of the name tag in percent.
     *
     * @param opacity new text opacity
     * @return {@code true} if the text opacity was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setTextOpacity(float opacity);

    /**
     * Checks if the text of the name tag is shadowed.
     *
     * @return {@code true} if the text is shadowed, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(pure = true)
    boolean isShadowed();

    /**
     * Sets whether the text of the name tag is shadowed.
     *
     * @param shadowed whether the text should be shadowed
     * @return {@code true} if the shadow was changed, {@code false} otherwise
     * @since 0.9.0
     */
    @Contract(mutates = "this")
    boolean setShadowed(boolean shadowed);

    /**
     * Checks if the name tag is visible through blocks.
     *
     * @return {@code true} if the name tag is see-through, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isSeeThrough();

    /**
     * Sets whether the name tag is visible through blocks.
     *
     * @param seeThrough whether the name tag should be see-through
     * @return {@code true} if the see-through state was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setSeeThrough(boolean seeThrough);

    /**
     * Gets the offset of the name tag relative to the character.
     *
     * @return offset
     * @since 0.1.0
     */
    @Contract(pure = true)
    Vector3f getOffset();

    /**
     * Sets the offset of the name tag relative to the character.
     *
     * @param offset new offset
     * @return {@code true} if the offset was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setOffset(Vector3f offset);

    /**
     * Sets the x offset of the name tag relative to the character.
     *
     * @param offset new x offset
     * @return {@code true} if the offset was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setOffsetX(float offset);

    /**
     * Sets the y offset of the name tag relative to the character.
     *
     * @param offset new y offset
     * @return {@code true} if the offset was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setOffsetY(float offset);

    /**
     * Sets the z offset of the name tag relative to the character.
     *
     * @param offset new z offset
     * @return {@code true} if the offset was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setOffsetZ(float offset);

    /**
     * Gets the scale of the name tag.
     *
     * @return scale
     * @since 0.1.0
     */
    @Contract(pure = true)
    Vector3f getScale();

    /**
     * Sets the scale of the name tag.
     *
     * @param scale new scale
     * @return {@code true} if the scale was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setScale(Vector3f scale);

    /**
     * Sets the uniform scale of the name tag.
     *
     * @param scale new scale
     * @return {@code true} if the scale was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setScale(float scale);

    /**
     * Gets the left rotation of the name tag.
     *
     * @return left rotation
     * @since 0.1.0
     */
    @Contract(pure = true)
    Quaternionf getLeftRotation();

    /**
     * Sets the left rotation of the name tag.
     *
     * @param rotation new left rotation
     * @return {@code true} if the rotation was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setLeftRotation(Quaternionf rotation);

    /**
     * Gets the right rotation of the name tag.
     *
     * @return right rotation
     * @since 0.1.0
     */
    @Contract(pure = true)
    Quaternionf getRightRotation();

    /**
     * Sets the right rotation of the name tag.
     *
     * @param rotation new right rotation
     * @return {@code true} if the rotation was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setRightRotation(Quaternionf rotation);
}
