package net.thenextlvl.character.plugin.character.attribute;

import core.nbt.serialization.ParserException;
import core.nbt.tag.Tag;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.attribute.Attribute;
import net.thenextlvl.character.attribute.AttributeType;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.BiConsumer;

public class PaperAttribute<V extends Entity, T> implements Attribute<T> {
    private final @NonNull AttributeType<T> type;
    private final @NonNull BiConsumer<Character<@NotNull V>, T> onChange;
    private final @NonNull Character<@NotNull V> character;
    private final @NonNull CharacterPlugin plugin;
    private T value;

    public PaperAttribute(@NonNull AttributeType<T> type, @NonNull BiConsumer<Character<@NotNull V>, T> onChange,
                          @NonNull Character<@NotNull V> character, @NonNull CharacterPlugin plugin) {
        this.type = type;
        this.onChange = onChange;
        this.character = character;
        this.plugin = plugin;
    }


    @Override
    public @NotNull AttributeType<T> getType() {
        return type;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean setValue(T value) {
        if (Objects.equals(this.value, value)) return false;
        onChange.accept(character, this.value = value);
        return true;
    }

    @Override
    public @NotNull Tag serialize() throws ParserException {
        return plugin.nbt().toTag(value);
    }

    @Override
    public void deserialize(@NotNull Tag tag) throws ParserException {
        setValue(plugin.nbt().fromTag(tag, type.getDataType()));
    }
}
