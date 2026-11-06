package net.thenextlvl.character.plugin.placeholder;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MiniPlaceholdersFormatter {
    public TagResolver tagResolver() {
        return MiniPlaceholders.audienceGlobalPlaceholders();
    }
}
