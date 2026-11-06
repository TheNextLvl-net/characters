package net.thenextlvl.character.plugin.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlaceholderAPIFormatter {
    public String format(final Player player, final String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
