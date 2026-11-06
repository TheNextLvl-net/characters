package net.thenextlvl.character.notification;

import net.kyori.adventure.title.Title;
import org.jspecify.annotations.Nullable;

/**
 * A title and subtitle in the MiniMessage format, parsed for every player it is shown to.
 *
 * @param title    the title
 * @param subtitle the subtitle
 * @param times    the times, or {@code null} for the default times
 * @since 0.9.0
 */
public record UnparsedTitle(String title, String subtitle, Title.@Nullable Times times) {
}
