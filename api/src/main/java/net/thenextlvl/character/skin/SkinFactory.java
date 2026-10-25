package net.thenextlvl.character.skin;

import com.destroystokyo.paper.profile.ProfileProperty;
import net.thenextlvl.binder.StaticBinder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Factory for creating skins.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface SkinFactory {
    /**
     * Gets the skin factory instance.
     *
     * @return skin factory
     * @since 0.9.0
     */
    static @CheckReturnValue SkinFactory factory() {
        return StaticBinder.getInstance(SkinFactory.class.getClassLoader()).find(SkinFactory.class);
    }

    /**
     * Creates a skin from the given image file.
     *
     * @param image the skin image
     * @param slim  whether the skin uses the slim model
     * @return a {@link CompletableFuture} that completes with the textures property of the skin
     * @since 0.1.0
     */
    CompletableFuture<ProfileProperty> skinFromFile(File image, boolean slim);

    /**
     * Creates a skin from the image at the given url.
     *
     * @param url  the url of the skin image
     * @param slim whether the skin uses the slim model
     * @return a {@link CompletableFuture} that completes with the textures property of the skin
     * @since 0.1.0
     */
    CompletableFuture<ProfileProperty> skinFromURL(URL url, boolean slim);
}
