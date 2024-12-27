package net.thenextlvl.character.plugin.model;

import com.destroystokyo.paper.PaperSkinParts;
import com.destroystokyo.paper.SkinParts;
import net.thenextlvl.character.SkinPartBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperSkinPartBuilder implements SkinPartBuilder {
    private byte raw;

    public PaperSkinPartBuilder() {
        this((byte) 127);
    }

    public PaperSkinPartBuilder(byte raw) {
        this.raw = raw;
    }

    @Override
    public PaperSkinPartBuilder all(boolean enabled) {
        this.raw = enabled ? (byte) 127 : 0;
        return this;
    }

    @Override
    public PaperSkinPartBuilder cape(boolean enabled) {
        if (enabled) this.raw |= 1;
        else this.raw &= ~1;
        return this;
    }

    @Override
    public PaperSkinPartBuilder jacket(boolean enabled) {
        if (enabled) this.raw |= 1 << 1;
        else this.raw &= ~(1 << 1);
        return this;
    }

    @Override
    public PaperSkinPartBuilder leftSleeve(boolean enabled) {
        if (enabled) this.raw |= 1 << 2;
        else this.raw &= ~(1 << 2);
        return this;
    }

    @Override
    public SkinPartBuilder raw(byte raw) {
        this.raw = raw;
        return this;
    }

    @Override
    public PaperSkinPartBuilder rightSleeve(boolean enabled) {
        if (enabled) this.raw |= 1 << 3;
        else this.raw &= ~(1 << 3);
        return this;
    }

    @Override
    public PaperSkinPartBuilder leftPants(boolean enabled) {
        if (enabled) this.raw |= 1 << 4;
        else this.raw &= ~(1 << 4);
        return this;
    }

    @Override
    public PaperSkinPartBuilder rightPants(boolean enabled) {
        if (enabled) this.raw |= 1 << 5;
        else this.raw &= ~(1 << 5);
        return this;
    }

    @Override
    public PaperSkinPartBuilder hat(boolean enabled) {
        if (enabled) this.raw |= 1 << 6;
        else this.raw &= ~(1 << 6);
        return this;
    }

    @Override
    public SkinParts build() {
        return new PaperSkinParts(raw);
    }
}
