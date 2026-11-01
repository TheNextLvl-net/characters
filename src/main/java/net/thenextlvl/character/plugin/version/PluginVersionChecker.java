package net.thenextlvl.character.plugin.version;

import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.version.SemanticVersion;
import net.thenextlvl.version.modrinth.paper.PaperModrinthVersionChecker;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PluginVersionChecker extends PaperModrinthVersionChecker<SemanticVersion> {
    public PluginVersionChecker(final Plugin plugin) {
        super(plugin, "oeBa7ep1");
    }

    @Override
    public String getLoader() {
        return CharacterPlugin.RUNNING_FOLIA ? "folia" : "paper";
    }

    @Override
    public SemanticVersion parseVersion(final String version) {
        return SemanticVersion.parse(version);
    }
}
