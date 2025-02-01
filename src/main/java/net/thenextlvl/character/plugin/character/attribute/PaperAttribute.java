package net.thenextlvl.character.plugin.character.attribute;

import com.google.common.base.Preconditions;
import core.nbt.serialization.ParserException;
import core.nbt.tag.Tag;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.attribute.Attribute;
import net.thenextlvl.character.attribute.AttributeType;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class PaperAttribute<E, T> implements Attribute<@NonNull E, T> {
    private final @NonNull AttributeType<@NonNull E, T> type;
    private final @NonNull Character<?> character;
    private final @NonNull CharacterPlugin plugin;
    private T value;

    public PaperAttribute(
            @NonNull AttributeType<E, T> type,
            @NonNull Character<?> character,
            @NonNull CharacterPlugin plugin
    ) {
        character.getEntity(type.entityType()).ifPresent(entity -> this.value = type.get(entity));
        this.character = character;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public @NonNull AttributeType<@NonNull E, T> getType() {
        return type;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean setValue(T value) {
        if (Objects.equals(this.getValue(), value)) return false;
        character.getEntity(type.entityType()).ifPresent(entity -> type.set(entity, value));
        this.value = value;
        return true;
    }

    @Override
    public @NonNull Tag serialize() throws ParserException {
        Preconditions.checkNotNull(value, "Cannot serialize attribute with null value");
        return plugin.nbt().toTag(value);
    }

    @Override
    public void deserialize(@NonNull Tag tag) throws ParserException {
        setValue(plugin.nbt().fromTag(tag, type.dataType()));
    }
}
