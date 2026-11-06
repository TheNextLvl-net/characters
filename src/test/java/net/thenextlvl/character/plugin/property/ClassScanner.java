package net.thenextlvl.character.plugin.property;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Lists classes of a package (recursively) from the classpath entry that contains a given anchor class,
 * without initializing them.
 */
@NullMarked
final class ClassScanner {
    private ClassScanner() {
    }

    static List<Class<?>> scan(final Class<?> anchor, final String packageName) {
        final var prefix = packageName.replace('.', '/') + '/';
        final var classes = new ArrayList<Class<?>>();
        for (final var name : entries(anchor)) {
            if (!name.startsWith(prefix) || !name.endsWith(".class")) continue;
            if (name.endsWith("module-info.class") || name.endsWith("package-info.class")) continue;
            final var className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
            try {
                classes.add(Class.forName(className, false, anchor.getClassLoader()));
            } catch (final ClassNotFoundException | LinkageError ignored) {
            }
        }
        return classes;
    }

    private static List<String> entries(final Class<?> anchor) {
        try {
            final var source = anchor.getProtectionDomain().getCodeSource();
            final var path = Path.of(source.getLocation().toURI());
            if (Files.isDirectory(path)) try (final Stream<Path> files = Files.walk(path)) {
                return files.filter(Files::isRegularFile)
                        .map(file -> path.relativize(file).toString().replace('\\', '/'))
                        .toList();
            }
            try (final var jar = new JarFile(path.toFile())) {
                return jar.stream().map(java.util.zip.ZipEntry::getName).toList();
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        } catch (final URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
