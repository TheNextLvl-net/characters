package net.thenextlvl.character.skin;

import com.destroystokyo.paper.SkinParts;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SkinPartBuilder {
    SkinPartBuilder all(boolean enabled);

    SkinPartBuilder cape(boolean enabled);

    SkinPartBuilder hat(boolean enabled);

    SkinPartBuilder jacket(boolean enabled);

    SkinPartBuilder leftPants(boolean enabled);

    SkinPartBuilder leftSleeve(boolean enabled);

    SkinPartBuilder rightPants(boolean enabled);

    SkinPartBuilder rightSleeve(boolean enabled);

    SkinPartBuilder toggle(SkinLayer layer, boolean enabled);

    SkinPartBuilder hide(SkinLayer layer);

    SkinPartBuilder show(SkinLayer layer);

    SkinPartBuilder parts(SkinParts parts);

    SkinPartBuilder raw(int raw);

    SkinParts build();
}
