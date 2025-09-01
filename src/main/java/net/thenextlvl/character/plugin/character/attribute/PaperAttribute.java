package net.thenextlvl.character.plugin.character.attribute;

import com.google.common.base.Preconditions;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.attribute.AttributeInstance;
import net.thenextlvl.character.attribute.AttributeType;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class PaperAttribute<E, T> implements AttributeInstance<T> {
    private final @NonNull AttributeType<@NonNull E, T> type;
    private final @NonNull Character<?> character;
    private final @NonNull CharacterPlugin plugin;
    private T value;

    public PaperAttribute(
            @NonNull AttributeType<E, T> type,
            @NonNull Character<?> character,
            @NonNull CharacterPlugin plugin
    ) {
        character.getEntity(type.entityType()).ifPresent(entity -> this.value = type.getOrDefault(entity));
        this.character = character;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public @NonNull AttributeType<@NonNull E, T> getType() {
        return type;
    }

    @Override
    public @NonNull Class<T> getDataType() {
        return type.dataType();
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean setValue(@Nullable T value) {
        if (Objects.equals(this.getValue(), value)) return false;
        character.getEntity(type.entityType()).ifPresent(entity -> type.set(entity, value));
        this.value = value;
        return true;
    }

    @Override
    public @NonNull Tag serialize() throws ParserException {
        Preconditions.checkNotNull(value, "Cannot serialize attribute with null value");
        return plugin.nbt().serialize(value);
    }

    @Override
    public void deserialize(@NonNull Tag tag) throws ParserException {
        setValue(plugin.nbt().deserialize(tag, type.dataType()));
    }
}
