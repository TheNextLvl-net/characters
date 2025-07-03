package net.thenextlvl.character.plugin.character;

import com.destroystokyo.paper.profile.ProfileProperty;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.skin.SkinFactory;
import net.thenextlvl.character.skin.SkinPartBuilder;
import org.mineskin.Java11RequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.Variant;
import org.mineskin.request.GenerateRequest;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

// https://docs.mineskin.org/docs/category/mineskin-api
public class PaperSkinFactory implements SkinFactory {
    private final MineSkinClient client;

    public PaperSkinFactory(CharacterPlugin plugin) {
        this.client = MineSkinClient.builder()
                .requestHandler((baseUrl, userAgent, apiKey, timeout, gson) ->
                        new Java11RequestHandler(baseUrl, userAgent, apiKey.isBlank() ? null : apiKey, timeout, gson))
                .userAgent("Characters/" + plugin.getPluginMeta().getVersion())
                .apiKey(Objects.requireNonNullElse(System.getenv("MINESKIN_API_KEY"), ""))
                .timeout(3000)
                .build();
    }

    @Override
    public CompletableFuture<ProfileProperty> skinFromFile(File image, boolean slim) {
        return submit(GenerateRequest.upload(image), slim);
    }

    @Override
    public CompletableFuture<ProfileProperty> skinFromURL(URL url, boolean slim) {
        return submit(GenerateRequest.url(url), slim);
    }

    @Override
    public SkinPartBuilder skinPartBuilder() {
        return new PaperSkinPartBuilder();
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
