package net.thenextlvl.character.plugin.property;

import net.thenextlvl.character.property.EntityProperties;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Discovers every piece of entity data vanilla synchronizes to the client
 * (every static {@code EntityDataAccessor} field of an entity class)
 * that is neither mapped to an entity property nor explicitly ignored in {@code synched-entity-data-coverage.txt}.
 * <p>
 * Synched entity data is everything the client renders, so this catches new visuals added by Mojang
 * even when the Bukkit API does not expose them yet.
 */
@NullMarked
class SynchedEntityDataCoverageTest {
    private static final String RESOURCE = "/synched-entity-data-coverage.txt";
    private static final String PACKAGE = "net.minecraft.world.entity";

    @Test
    void everySynchedEntityDataIsCoveredOrIgnored() throws ClassNotFoundException {
        final var loader = getClass().getClassLoader();
        final var entity = Class.forName(PACKAGE + ".Entity", false, loader);
        final var accessor = Class.forName("net.minecraft.network.syncher.EntityDataAccessor", false, loader);

        final var fields = new TreeSet<String>();
        for (final var type : ClassScanner.scan(entity, PACKAGE)) {
            if (!entity.isAssignableFrom(type)) continue;
            Arrays.stream(type.getDeclaredFields())
                    .filter(field -> Modifier.isStatic(field.getModifiers()))
                    .filter(field -> accessor.isAssignableFrom(field.getType()))
                    .forEach(field -> fields.add(name(type) + "#" + field.getName()));
        }
        if (fields.isEmpty()) fail("No synched entity data found, is the server on the test classpath?");

        final var coverage = CoverageFile.read(RESOURCE);
        final var keys = EntityProperties.values().stream()
                .map(property -> property.key().asString())
                .collect(Collectors.toSet());
        final var problems = new ArrayList<String>();
        coverage.aliases().forEach((field, mapped) -> {
            if (!fields.contains(field)) problems.add("mapped field " + field + " does not exist (anymore)");
            for (final var key : mapped.split(",")) {
                if (!keys.contains(key.strip())) problems.add("field " + field + " points to unknown property " + key.strip());
            }
        });
        coverage.ignored().keySet().forEach(field -> {
            if (!fields.contains(field)) problems.add("ignored field " + field + " does not exist (anymore)");
        });

        final var missing = fields.stream()
                .filter(field -> !coverage.aliases().containsKey(field))
                .filter(field -> !coverage.ignored().containsKey(field))
                .toList();

        if (problems.isEmpty() && missing.isEmpty()) return;
        final var message = new StringBuilder();
        if (!problems.isEmpty()) message.append("Problems:\n  ").append(String.join("\n  ", problems)).append('\n');
        if (!missing.isEmpty()) message.append(missing.size())
                .append(" synched entity data fields are not mapped, map them to properties or list them in ")
                .append(RESOURCE).append(":\n  ").append(String.join("\n  ", missing));
        fail(message.toString());
    }

    // class name relative to net.minecraft.world.entity, e.g. animal.feline.Cat
    private static String name(final Class<?> type) {
        return type.getName().substring(PACKAGE.length() + 1).replace('$', '.');
    }
}
