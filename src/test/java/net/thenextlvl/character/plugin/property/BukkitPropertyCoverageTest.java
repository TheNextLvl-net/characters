package net.thenextlvl.character.plugin.property;

import net.thenextlvl.character.property.EntityProperties;
import net.thenextlvl.character.property.EntityProperty;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Discovers getter/setter pairs on the entity API that are neither covered by an {@link EntityProperty}
 * nor explicitly ignored in {@code bukkit-property-coverage.txt}.
 * <p>
 * Properties are matched by convention: {@code cat:lying_down} covers {@code Cat#setLyingDown}.
 * Anything that does not follow the convention needs an alias entry.
 */
@NullMarked
class BukkitPropertyCoverageTest {
    private static final String RESOURCE = "/bukkit-property-coverage.txt";
    private static final List<String> PACKAGES = List.of(
            "org.bukkit.entity",
            "io.papermc.paper.entity",
            "com.destroystokyo.paper.entity"
    );
    // super interfaces from these packages are not scanned, they never hold entity state worth persisting
    private static final List<String> EXCLUDED_PACKAGES = List.of(
            "java.",
            "net.kyori.",
            "org.bukkit.command",
            "org.bukkit.metadata",
            "org.bukkit.permissions",
            "org.bukkit.persistence",
            "org.bukkit.plugin"
    );

    @Test
    void everyEntityPropertyIsCoveredOrIgnored() {
        final var coverage = CoverageFile.read(RESOURCE);
        final var types = discoverTypes();
        final var pairs = discoverPairs(types);
        final var problems = new ArrayList<String>();

        final var covered = new HashMap<String, String>();
        for (final var property : EntityProperties.values()) {
            final var entry = conventionalEntry(property);
            if (pairs.containsKey(entry)) covered.put(entry, property.key().asString());
            else if (!coverage.aliases().containsValue(property.key().asString()))
                problems.add("property " + property.key().asString() + " matches no API pair (expected " + entry + "), add an alias");
        }

        final var keys = EntityProperties.values().stream()
                .map(property -> property.key().asString())
                .collect(Collectors.toSet());
        coverage.aliases().forEach((entry, key) -> {
            if (!pairs.containsKey(entry) && !isMethod(types, entry))
                problems.add("alias " + entry + " is neither an API pair nor a method (anymore)");
            if (!keys.contains(key)) problems.add("alias " + entry + " points to unknown property " + key);
            if (covered.containsKey(entry)) problems.add("alias " + entry + " is redundant, it already matches by convention");
        });
        coverage.ignored().keySet().forEach(entry -> {
            if (!pairs.containsKey(entry)) problems.add("ignored entry " + entry + " does not exist (anymore)");
            if (covered.containsKey(entry)) problems.add("ignored entry " + entry + " is covered by " + covered.get(entry));
        });

        final var missing = new TreeSet<String>();
        for (final var entry : pairs.keySet()) {
            if (covered.containsKey(entry)) continue;
            if (coverage.aliases().containsKey(entry)) continue;
            if (coverage.ignored().containsKey(entry)) continue;
            missing.add(entry + " (" + pairs.get(entry).getTypeName() + ")");
        }

        if (problems.isEmpty() && missing.isEmpty()) return;
        final var message = new StringBuilder();
        if (!problems.isEmpty()) message.append("Problems:\n  ").append(String.join("\n  ", problems)).append('\n');
        if (!missing.isEmpty()) message.append(missing.size())
                .append(" API pairs are not covered by a property, add one or list them in ").append(RESOURCE).append(":\n  ")
                .append(String.join("\n  ", missing));
        fail(message.toString());
    }

    private static String conventionalEntry(final EntityProperty<?, ?> property) {
        return typeName(property.entityType()) + "#" + camelCase(property.key().value());
    }

    private static Map<String, Class<?>> discoverTypes() {
        final var types = new LinkedHashSet<Class<?>>();
        PACKAGES.forEach(packageName -> ClassScanner.scan(Entity.class, packageName).stream()
                .filter(Class::isInterface)
                .filter(type -> Entity.class.isAssignableFrom(type))
                .forEach(type -> collect(type, types)));

        final var names = new HashMap<String, Class<?>>();
        for (final var type : types) {
            final var previous = names.put(typeName(type), type);
            if (previous != null && previous != type)
                throw new IllegalStateException("Ambiguous type name " + typeName(type) + ": " + previous + ", " + type);
        }
        return names;
    }

    // entry -> value type
    private static Map<String, Class<?>> discoverPairs(final Map<String, Class<?>> types) {
        final var pairs = new TreeMap<String, Class<?>>();
        types.forEach((name, type) -> pairs(type).forEach((pair, valueType) -> pairs.put(name + "#" + pair, valueType)));
        return pairs;
    }

    // an alias may also point at a single method, e.g. Allay#startDancing
    private static boolean isMethod(final Map<String, Class<?>> types, final String entry) {
        final var split = entry.indexOf('#');
        if (split == -1) return false;
        final var type = types.get(entry.substring(0, split));
        final var method = entry.substring(split + 1);
        return type != null && Arrays.stream(type.getDeclaredMethods()).anyMatch(m -> m.getName().equals(method));
    }

    // collects the type and all of its super interfaces, except those from excluded packages
    private static void collect(final Class<?> type, final Set<Class<?>> types) {
        if (EXCLUDED_PACKAGES.stream().anyMatch(type.getName()::startsWith) || !types.add(type)) return;
        for (final var parent : type.getInterfaces()) collect(parent, types);
    }

    private static Map<String, Class<?>> pairs(final Class<?> type) {
        final Map<String, List<Method>> methods = Arrays.stream(type.getDeclaredMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .filter(method -> !method.isDefault() && !method.isSynthetic() && !method.isBridge())
                .filter(method -> !method.isAnnotationPresent(Deprecated.class))
                .collect(Collectors.groupingBy(Method::getName));

        final var pairs = new TreeMap<String, Class<?>>();
        methods.forEach((name, overloads) -> overloads.forEach(setter -> {
            if (setter.getParameterCount() != 1) return;
            final var valueType = setter.getParameterTypes()[0];
            if (name.startsWith("set") && name.length() > 3 && Character.isUpperCase(name.charAt(3))) {
                final var suffix = name.substring(3);
                for (final var prefix : List.of("get", "is", "has", "")) {
                    final var getterName = prefix.isEmpty() ? decapitalize(suffix) : prefix + suffix;
                    if (hasGetter(methods, getterName, valueType)) {
                        pairs.put(decapitalize(suffix), valueType);
                        return;
                    }
                }
            } else if (!isAccessorPrefix(name) && hasGetter(methods, name, valueType)) {
                pairs.put(name, valueType); // adventure style, e.g. text(Component) and text()
            }
        }));
        return pairs;
    }

    private static boolean hasGetter(final Map<String, List<Method>> methods, final String name, final Class<?> valueType) {
        return methods.getOrDefault(name, List.of()).stream().anyMatch(getter ->
                getter.getParameterCount() == 0 && box(getter.getReturnType()).equals(box(valueType)));
    }

    private static boolean isAccessorPrefix(final String name) {
        for (final var prefix : List.of("set", "get", "is", "has"))
            if (name.startsWith(prefix) && name.length() > prefix.length()
                && Character.isUpperCase(name.charAt(prefix.length()))) return true;
        return false;
    }

    private static Class<?> box(final Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == boolean.class) return Boolean.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        if (type == char.class) return Character.class;
        return Void.class;
    }

    // simple name for org.bukkit.entity, fully qualified otherwise (Bukkit and Paper both have a Shearable)
    static String typeName(final Class<?> type) {
        final var name = type.getName().replace('$', '.');
        return type.getPackageName().equals("org.bukkit.entity")
                ? name.substring("org.bukkit.entity.".length())
                : name;
    }

    private static String camelCase(final String snake) {
        final var builder = new StringBuilder();
        var upper = false;
        for (final var c : snake.toCharArray()) {
            if (c == '_') upper = true;
            else {
                builder.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return builder.toString();
    }

    private static String decapitalize(final String name) {
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) return name; // e.g. AI
        return name.substring(0, 1).toLowerCase(Locale.ROOT) + name.substring(1);
    }
}
