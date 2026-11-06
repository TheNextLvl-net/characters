package net.thenextlvl.character.property;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/**
 * A property value that is resolved separately for every viewer of a character.
 * <p>
 * Every viewer has their own entity, so a viewer value can make each player see something different,
 * for example their own skin on a mannequin.
 * Viewer values are persisted by their key, see {@link ViewerValues}.
 *
 * @param <T> the value type
 * @see ViewerValues
 * @see net.thenextlvl.character.Character#setViewerValue(EntityProperty, ViewerValue)
 * @since 0.9.0
 */
public final class ViewerValue<T> implements Keyed {
    private final Key key;
    private final Class<T> valueType;
    private final Function<? super Player, ? extends @Nullable T> resolver;

    ViewerValue(final Key key, final Class<T> valueType, final Function<? super Player, ? extends @Nullable T> resolver) {
        this.key = key;
        this.valueType = valueType;
        this.resolver = resolver;
    }

    /**
     * Returns the key of this viewer value.
     *
     * @return the key
     * @since 0.9.0
     */
    @Override
    @Contract(pure = true)
    public Key key() {
        return key;
    }

    /**
     * Returns the type of the resolved value.
     *
     * @return the value type
     * @since 0.9.0
     */
    @Contract(pure = true)
    public Class<T> valueType() {
        return valueType;
    }

    /**
     * Resolves the value for the given viewer.
     * <p>
     * This is called on the region of the viewer.
     *
     * @param viewer the viewer
     * @return the resolved value
     * @since 0.9.0
     */
    public @Nullable T resolve(final Player viewer) {
        return resolver.apply(viewer);
    }

    /**
     * Checks if this viewer value can be used for the given property.
     *
     * @param property the property
     * @return {@code true} if the resolved value fits the value type of the property
     * @since 0.9.0
     */
    @Contract(pure = true)
    public boolean isApplicable(final EntityProperty<?, ?> property) {
        return property.valueType().isAssignableFrom(valueType);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        return o instanceof final ViewerValue<?> that && key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "ViewerValue{key=" + key.asString() + '}';
    }
}
