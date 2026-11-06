package net.thenextlvl.character.property;

import com.google.common.base.Preconditions;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * All registered viewer values.
 * <p>
 * Characters persist viewer values by key.
 * A character that references a viewer value that is not registered (yet) keeps the reference
 * and ignores it until it is registered.
 *
 * @see ViewerValue
 * @since 0.9.0
 */
public final class ViewerValues {
    private static final Map<Key, ViewerValue<?>> values = new ConcurrentHashMap<>();

    /**
     * The profile of the viewer, makes every player see a mannequin with their own skin.
     *
     * @see EntityProperties#MANNEQUIN_PROFILE
     * @since 0.9.0
     */
    public static final ViewerValue<ResolvableProfile> PROFILE = register(Key.key("viewer", "profile"),
            ResolvableProfile.class, viewer -> ResolvableProfile.resolvableProfile(viewer.getPlayerProfile()));

    private ViewerValues() {
    }

    /**
     * Registers a new viewer value.
     *
     * @param key       the key the value is persisted by
     * @param valueType the type of the resolved value
     * @param resolver  resolves the value for a viewer, called on the region of the viewer
     * @param <T>       the value type
     * @return the registered viewer value
     * @throws IllegalArgumentException if a viewer value with the given key is already registered
     * @since 0.9.0
     */
    public static <T> ViewerValue<T> register(final Key key, final Class<T> valueType,
                                              final Function<? super Player, ? extends @Nullable T> resolver) {
        final var value = new ViewerValue<>(key, valueType, resolver);
        Preconditions.checkArgument(values.putIfAbsent(key, value) == null, "Viewer value %s is already registered", key);
        return value;
    }

    /**
     * Returns all registered viewer values.
     *
     * @return all viewer values
     * @since 0.9.0
     */
    @Unmodifiable
    @Contract(pure = true)
    public static List<ViewerValue<?>> values() {
        return List.copyOf(values.values());
    }

    /**
     * Returns the viewer value with the given key.
     *
     * @param key the key
     * @return the viewer value, or empty if none is registered with the given key
     * @since 0.9.0
     */
    @Contract(pure = true)
    public static Optional<ViewerValue<?>> getByKey(final Key key) {
        return Optional.ofNullable(values.get(key));
    }
}
