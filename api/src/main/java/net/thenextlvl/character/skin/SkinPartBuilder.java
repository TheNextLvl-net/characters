package net.thenextlvl.character.skin;

import com.destroystokyo.paper.SkinParts;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SkinPartBuilder {
    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder all(boolean enabled);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder cape(boolean enabled);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder hat(boolean enabled);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder jacket(boolean enabled);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder leftPants(boolean enabled);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder leftSleeve(boolean enabled);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder rightPants(boolean enabled);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder rightSleeve(boolean enabled);

    @Contract(value = "_, _ -> this", mutates = "this")
    SkinPartBuilder toggle(SkinLayer layer, boolean enabled);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder hide(SkinLayer layer);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder show(SkinLayer layer);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder parts(SkinParts parts);

    @Contract(value = "_ -> this", mutates = "this")
    SkinPartBuilder raw(int raw);

    @Contract(value = " -> new", pure = true)
    SkinParts build();
}
