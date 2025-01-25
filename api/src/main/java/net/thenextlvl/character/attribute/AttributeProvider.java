package net.thenextlvl.character.attribute;

import net.thenextlvl.character.Character;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.function.BiConsumer;

@NullMarked
public interface AttributeProvider {
    <V extends Entity, T> void register(
            AttributeType<T> type, Class<V> target,
            BiConsumer<Character<V>, T> setter
    );

    @Unmodifiable
    Set<AttributeType<?>> getAttributeTypes();

    boolean isRegistered(AttributeType<?> type);

    boolean unregister(AttributeType<?> type);
}
