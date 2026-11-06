package net.thenextlvl.character.plugin.property;

import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Reads a coverage file mapping an entry to either a property key or a reason for ignoring it.
 * <pre>
 * # comment
 * Cat#catType = cat:variant
 * Entity#velocity = ignored: runtime state, not a visual
 * </pre>
 */
@NullMarked
record CoverageFile(Map<String, String> aliases, Map<String, String> ignored) {
    private static final String IGNORED = "ignored:";

    static CoverageFile read(final String resource) {
        final var aliases = new LinkedHashMap<String, String>();
        final var ignored = new LinkedHashMap<String, String>();
        final var stream = Objects.requireNonNull(CoverageFile.class.getResourceAsStream(resource), resource);
        try (final var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            var number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) continue;
                final var split = line.indexOf('=');
                if (split == -1) throw new IllegalStateException(resource + ":" + number + ": missing '='");
                final var key = line.substring(0, split).strip();
                final var value = line.substring(split + 1).strip();
                final var target = value.startsWith(IGNORED) ? ignored : aliases;
                final var previous = target.put(key, value.startsWith(IGNORED) ? value.substring(IGNORED.length()).strip() : value);
                if (previous != null || (target == ignored ? aliases : ignored).containsKey(key))
                    throw new IllegalStateException(resource + ":" + number + ": duplicate entry " + key);
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return new CoverageFile(aliases, ignored);
    }
}
