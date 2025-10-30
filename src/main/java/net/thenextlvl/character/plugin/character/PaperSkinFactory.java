package net.thenextlvl.character.plugin.character;

import com.destroystokyo.paper.profile.ProfileProperty;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.skin.SkinFactory;
import org.jspecify.annotations.NullMarked;
import org.mineskin.Java11RequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.Variant;
import org.mineskin.request.GenerateRequest;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

// https://docs.mineskin.org/docs/category/mineskin-api
@NullMarked
public final class PaperSkinFactory implements SkinFactory {
    private final MineSkinClient client;

    public PaperSkinFactory(CharacterPlugin plugin) {
        var apiKey = System.getenv("MINESKIN_API_KEY");
        this.client = MineSkinClient.builder()
                .requestHandler(Java11RequestHandler::new)
                .userAgent("Characters/" + plugin.getPluginMeta().getVersion())
                .apiKey(apiKey)
                .timeout(3000)
                .build();
        if (apiKey != null && !apiKey.isBlank()) return;
        plugin.getComponentLogger().warn("You can define an API key via the environment variable MINESKIN_API_KEY");
        plugin.getComponentLogger().warn("If you don't plan on using the skin file or url service a lot you can ignore this warning");
    }

    @Override
    public CompletableFuture<ProfileProperty> skinFromFile(File image, boolean slim) {
        return submit(GenerateRequest.upload(image), slim);
    }

    @Override
    public CompletableFuture<ProfileProperty> skinFromURL(URL url, boolean slim) {
        return submit(GenerateRequest.url(url), slim);
    }

    private CompletableFuture<ProfileProperty> submit(GenerateRequest request, boolean slim) {
        return client.queue().submit(request.variant(slim ? Variant.SLIM : Variant.AUTO))
                .thenCompose(response -> response.getJob().waitForCompletion(client))
                .thenCompose(jobReference -> jobReference.getOrLoadSkin(client))
                .thenApply(skinInfo -> {
                    var data = skinInfo.texture().data();
                    return new ProfileProperty("textures", data.value(), data.signature());
                });
    }
}
