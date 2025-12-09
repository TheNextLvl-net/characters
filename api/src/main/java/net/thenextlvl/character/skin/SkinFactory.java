package net.thenextlvl.character.skin;

import com.destroystokyo.paper.profile.ProfileProperty;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public interface SkinFactory {
    CompletableFuture<ProfileProperty> skinFromFile(File image, boolean slim);

    CompletableFuture<ProfileProperty> skinFromURL(URL url, boolean slim);
}
