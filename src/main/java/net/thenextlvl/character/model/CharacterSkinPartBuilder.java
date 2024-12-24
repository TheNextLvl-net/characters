package net.thenextlvl.character.model;

import com.destroystokyo.paper.SkinParts;
import net.thenextlvl.character.skin.SkinPartBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterSkinPartBuilder implements SkinPartBuilder {
    private byte raw;

    public CharacterSkinPartBuilder() {
        this((byte) 127);
    }

    public CharacterSkinPartBuilder(byte raw) {
        this.raw = raw;
    }

    @Override
    public CharacterSkinPartBuilder cape(boolean enabled) {
        if (enabled) raw |= 1;
        else raw &= ~1;
        return this;
    }

    @Override
    public CharacterSkinPartBuilder jacket(boolean enabled) {
        if (enabled) raw |= 1 << 1;
        else raw &= ~(1 << 1);
        return this;
    }

    @Override
    public CharacterSkinPartBuilder leftSleeve(boolean enabled) {
        if (enabled) raw |= 1 << 2;
        else raw &= ~(1 << 2);
        return this;
    }

    @Override
    public CharacterSkinPartBuilder rightSleeve(boolean enabled) {
        if (enabled) raw |= 1 << 3;
        else raw &= ~(1 << 3);
        return this;
    }

    @Override
    public CharacterSkinPartBuilder leftPants(boolean enabled) {
        if (enabled) raw |= 1 << 4;
        else raw &= ~(1 << 4);
        return this;
    }

    @Override
    public CharacterSkinPartBuilder rightPants(boolean enabled) {
        if (enabled) raw |= 1 << 5;
        else raw &= ~(1 << 5);
        return this;
    }

    @Override
    public CharacterSkinPartBuilder hat(boolean enabled) {
        if (enabled) raw |= 1 << 6;
        else raw &= ~(1 << 6);
        return this;
    }

    @Override
    public SkinParts build() {
        return new CharacterSkinParts(raw);
    }
}
