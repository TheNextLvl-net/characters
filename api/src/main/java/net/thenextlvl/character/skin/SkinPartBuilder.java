package net.thenextlvl.character.skin;

import com.destroystokyo.paper.SkinParts;
import org.jspecify.annotations.NullMarked;

import java.util.ServiceLoader;

@NullMarked
public interface SkinPartBuilder {
    static SkinPartBuilder builder() {
        return ServiceLoader.load(SkinPartBuilder.class, SkinPartBuilder.class.getClassLoader()).findFirst()
                .orElseThrow(() -> new IllegalStateException("No implementation of SkinPartBuilder found"));
    }

    static SkinPartBuilder of(SkinParts parts) {
        return builder().raw(parts.getRaw());
    }

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

    SkinPartBuilder raw(int raw);

    SkinParts build();
}
