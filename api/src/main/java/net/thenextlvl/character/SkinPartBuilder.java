package net.thenextlvl.character;

import com.destroystokyo.paper.SkinParts;
import org.jspecify.annotations.NullMarked;

import java.util.ServiceLoader;

@NullMarked
public interface SkinPartBuilder {
    static SkinPartBuilder builder() {
        return ServiceLoader.load(SkinPartBuilder.class, SkinPartBuilder.class.getClassLoader()).findFirst()
                .orElseThrow(() -> new IllegalStateException("No implementation of SkinPartBuilder found"));
    }

    SkinPartBuilder all(boolean enabled);

    SkinPartBuilder cape(boolean enabled);

    SkinPartBuilder hat(boolean enabled);

    SkinPartBuilder jacket(boolean enabled);

    SkinPartBuilder leftPants(boolean enabled);

    SkinPartBuilder leftSleeve(boolean enabled);

    SkinPartBuilder raw(byte raw);

    SkinPartBuilder rightPants(boolean enabled);

    SkinPartBuilder rightSleeve(boolean enabled);

    SkinParts build();
}
