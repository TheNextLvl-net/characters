package net.thenextlvl.character.plugin.character.attribute;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.attribute.Attribute;
import net.thenextlvl.character.attribute.AttributeProvider;
import net.thenextlvl.character.attribute.AttributeType;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
public class PaperAttributeProvider implements AttributeProvider {
    private final Map<AttributeType<?>, Function<Character<?>, @Nullable Attribute<?>>> attributes = new HashMap<>();
    private final CharacterPlugin plugin;

    public PaperAttributeProvider(CharacterPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<Attribute<T>> createAttribute(AttributeType<T> type, Character<?> character) {
        return Optional.ofNullable(attributes.get(type)).map(function ->
                (Attribute<T>) function.apply(character));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Entity, T> void register(
            AttributeType<T> type, Class<V> target,
            BiConsumer<Character<V>, T> onChange
    ) {
        Preconditions.checkArgument(!isRegistered(type), "Attribute type already registered: %s", type);
        attributes.put(type, character -> {
            var entityClass = character.getType().getEntityClass();
            if (entityClass == null || !target.isAssignableFrom(entityClass)) return null;
            return new PaperAttribute<>(type, onChange, (Character<V>) character, plugin);
        });
    }

    @Override
    public @Unmodifiable Set<AttributeType<?>> getAttributeTypes() {
        return Set.copyOf(attributes.keySet());
    }

    @Override
    public boolean isRegistered(AttributeType<?> type) {
        return attributes.containsKey(type);
    }

    @Override
    public boolean unregister(AttributeType<?> type) {
        return attributes.remove(type) != null;
    }
}
