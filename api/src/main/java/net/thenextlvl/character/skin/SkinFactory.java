package net.thenextlvl.character.skin;

import com.destroystokyo.paper.profile.ProfileProperty;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public interface SkinFactory {
    CompletableFuture<ProfileProperty> fromFile(File image, boolean slim);

    CompletableFuture<ProfileProperty> fromURL(URL url, boolean slim);
}
