package net.thenextlvl.character.plugin.model;

import com.destroystokyo.paper.PaperSkinParts;
import com.destroystokyo.paper.SkinParts;
import net.thenextlvl.character.skin.SkinLayer;
import net.thenextlvl.character.skin.SkinPartBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperSkinPartBuilder implements SkinPartBuilder {
    private int raw;

    public PaperSkinPartBuilder() {
        this(127);
    }

    public PaperSkinPartBuilder(int raw) {
        this.raw = raw;
    }

    @Override
    public SkinPartBuilder all(boolean enabled) {
        return raw(enabled ? 127 : 0);
    }

    @Override
    public SkinPartBuilder cape(boolean enabled) {
        return toggle(SkinLayer.CAPE, enabled);
    }

    @Override
    public SkinPartBuilder jacket(boolean enabled) {
        return toggle(SkinLayer.JACKET, enabled);
    }

    @Override
    public SkinPartBuilder leftSleeve(boolean enabled) {
        return toggle(SkinLayer.LEFT_SLEEVE, enabled);
    }

    @Override
    public SkinPartBuilder rightSleeve(boolean enabled) {
        return toggle(SkinLayer.RIGHT_SLEEVE, enabled);
    }

    @Override
    public SkinPartBuilder leftPants(boolean enabled) {
        return toggle(SkinLayer.LEFT_PANTS_LEG, enabled);
    }

    @Override
    public SkinPartBuilder rightPants(boolean enabled) {
        return toggle(SkinLayer.RIGHT_PANTS_LEG, enabled);
    }

    @Override
    public SkinPartBuilder hat(boolean enabled) {
        return toggle(SkinLayer.HAT, enabled);
    }

    @Override
    public SkinPartBuilder toggle(SkinLayer layer, boolean enabled) {
        return enabled ? show(layer) : hide(layer);
    }

    @Override
    public SkinPartBuilder hide(SkinLayer layer) {
        return raw(raw &= ~layer.getMask());
    }

    @Override
    public SkinPartBuilder show(SkinLayer layer) {
        return raw(raw |= layer.getMask());
    }

    @Override
    public SkinPartBuilder raw(int raw) {
        this.raw = raw;
        return this;
    }

    @Override
    public SkinParts build() {
        return new PaperSkinParts(raw);
    }
}
