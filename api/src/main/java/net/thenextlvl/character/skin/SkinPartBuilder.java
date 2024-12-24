package net.thenextlvl.character.skin;

import com.destroystokyo.paper.SkinParts;
import org.jspecify.annotations.NullMarked;

import java.util.ServiceLoader;

@NullMarked
public interface SkinPartBuilder {
    static SkinPartBuilder builder() {
        return ServiceLoader.load(SkinPartBuilder.class).findFirst()
                .orElseThrow(() -> new IllegalStateException("No SkinPartBuilder service found"));
    }
    
    SkinPartBuilder cape(boolean enabled);

    SkinPartBuilder jacket(boolean enabled);

    SkinPartBuilder leftSleeve(boolean enabled);

    SkinPartBuilder rightSleeve(boolean enabled);

    SkinPartBuilder leftPants(boolean enabled);

    SkinPartBuilder rightPants(boolean enabled);

    SkinPartBuilder hat(boolean enabled);

    SkinParts build();
}
