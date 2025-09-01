package net.thenextlvl.character.codec;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.adapter.BooleanAdapter;
import core.nbt.serialization.adapter.DoubleAdapter;
import core.nbt.serialization.adapter.EnumAdapter;
import core.nbt.serialization.adapter.FloatAdapter;
import core.nbt.serialization.adapter.IntegerAdapter;
import core.nbt.serialization.adapter.LongAdapter;
import core.nbt.serialization.adapter.StringAdapter;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

@NullMarked
final class SimpleEntityCodec<E, T> implements EntityCodec<E, T> {
    private final Key key;
    private final Class<E> entityType;
    private final Class<T> valueType;
    private final Function<E, T> getter;
    private final BiPredicate<E, T> setter;
    private final ArgumentType<T> argumentType;
    private final TagAdapter<T> adapter;

    SimpleEntityCodec(Key key,
                      Class<E> entityType,
                      Class<T> valueType,
                      Function<E, T> getter,
                      BiPredicate<E, T> setter,
                      ArgumentType<T> argumentType,
                      TagAdapter<T> adapter
    ) {
        this.key = Objects.requireNonNull(key, "key");
        this.entityType = Objects.requireNonNull(entityType, "entityType");
        this.valueType = Objects.requireNonNull(valueType, "valueType");
        this.getter = Objects.requireNonNull(getter, "getter");
        this.setter = Objects.requireNonNull(setter, "setter");
        this.argumentType = Objects.requireNonNull(argumentType, "argumentType");
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    @Override
    public Key key() {
        return key;
    }

    @Override
    public Class<E> entityType() {
        return entityType;
    }

    @Override
    public Class<T> valueType() {
        return valueType;
    }

    @Override
    public Function<E, T> getter() {
        return getter;
    }

    @Override
    public BiPredicate<E, T> setter() {
        return setter;
    }

    @Override
    public ArgumentType<T> argumentType() {
        return argumentType;
    }

    @Override
    public TagAdapter<T> adapter() {
        return adapter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleEntityCodec<?, ?> that = (SimpleEntityCodec<?, ?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    @Override
    public String toString() {
        return "SimpleEntityCodec{" +
               "valueType=" + valueType +
               ", entityType=" + entityType +
               ", key=" + key +
               '}';
    }

    static <E> Builder<E, Boolean> booleanCodec(Key key, Class<E> entityType) {
        return new Builder<>(key, entityType, boolean.class)
                .argumentType(BoolArgumentType.bool())
                .adapter(BooleanAdapter.INSTANCE);
    }

    static <E> Builder<E, Long> longCodec(Key key, Class<E> entityType, long min, long max) {
        return new Builder<>(key, entityType, long.class)
                .argumentType(LongArgumentType.longArg(min, max))
                .adapter(LongAdapter.INSTANCE);
    }

    static <E> Builder<E, Double> doubleCodec(Key key, Class<E> entityType, double min, double max) {
        return new Builder<>(key, entityType, double.class)
                .argumentType(DoubleArgumentType.doubleArg(min, max))
                .adapter(DoubleAdapter.INSTANCE);
    }

    static <E> Builder<E, Float> floatCodec(Key key, Class<E> entityType, float min, float max) {
        return new Builder<>(key, entityType, float.class)
                .argumentType(FloatArgumentType.floatArg(min, max))
                .adapter(FloatAdapter.INSTANCE);
    }

    static <E> Builder<E, Integer> intCodec(Key key, Class<E> entityType, int min, int max) {
        return new Builder<>(key, entityType, int.class)
                .argumentType(IntegerArgumentType.integer(min, max))
                .adapter(IntegerAdapter.INSTANCE);
    }

    static <E> Builder<E, String> stringCodec(Key key, Class<E> entityType) {
        return new Builder<>(key, entityType, String.class)
                .argumentType(StringArgumentType.string())
                .adapter(StringAdapter.INSTANCE);
    }

    static <E, T extends Enum<T>> Builder<E, T> enumCodec(Key key, Class<E> entityType, Class<T> enumType) {
        return new Builder<>(key, entityType, enumType)
                .argumentType(EnumArgumentType.of(enumType, EnumStringCodec.lowerHyphen()))
                .adapter(new EnumAdapter<>(enumType));
    }

    static final class Builder<E, T> implements EntityCodec.Builder<E, T> {
        private final Key key;
        private final Class<E> entityType;
        private final Class<T> valueType;

        private @Nullable Function<E, T> getter;
        private @Nullable BiPredicate<E, T> setter;
        private @Nullable ArgumentType<T> argumentType;
        private @Nullable TagAdapter<T> adapter;

        Builder(Key key, Class<E> entityType, Class<T> valueType) {
            this.key = Objects.requireNonNull(key, "key");
            this.entityType = Objects.requireNonNull(entityType, "entityType");
            this.valueType = Objects.requireNonNull(valueType, "valueType");
        }

        public Builder<E, T> getter(Function<E, T> getter) {
            this.getter = getter;
            return this;
        }

        public Builder<E, T> setter(BiConsumer<E, T> setter) {
            Preconditions.checkArgument(getter != null, "getter");
            return setter((e, t) -> {
                var apply = getter.apply(e);
                setter.accept(e, t);
                return getter.apply(e) != apply;
            });
        }

        public Builder<E, T> setter(BiPredicate<E, T> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<E, T> argumentType(ArgumentType<T> argumentType) {
            this.argumentType = argumentType;
            return this;
        }

        @Override
        public Builder<E, T> adapter(TagAdapter<T> adapter) {
            this.adapter = adapter;
            return this;
        }

        public EntityCodec<E, T> build() {
            Preconditions.checkArgument(getter != null, "getter");
            Preconditions.checkArgument(setter != null, "setter");
            Preconditions.checkArgument(argumentType != null, "argumentType");
            Preconditions.checkArgument(adapter != null, "adapter");
            return new SimpleEntityCodec<>(key, entityType, valueType, getter, setter, argumentType, adapter);
        }
    }
}
