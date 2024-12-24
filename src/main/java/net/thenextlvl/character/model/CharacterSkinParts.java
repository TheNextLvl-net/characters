package net.thenextlvl.character.model;

import com.destroystokyo.paper.SkinParts;

public record CharacterSkinParts(byte raw) implements SkinParts {
    @Override
    public boolean hasCapeEnabled() {
        return (raw & 1) != 0;
    }

    @Override
    public boolean hasJacketEnabled() {
        return (raw & 1 << 1) != 0;
    }

    @Override
    public boolean hasLeftSleeveEnabled() {
        return (raw & 1 << 2) != 0;
    }

    @Override
    public boolean hasRightSleeveEnabled() {
        return (raw & 1 << 3) != 0;
    }

    @Override
    public boolean hasLeftPantsEnabled() {
        return (raw & 1 << 4) != 0;
    }

    @Override
    public boolean hasRightPantsEnabled() {
        return (raw & 1 << 5) != 0;
    }

    @Override
    public boolean hasHatsEnabled() {
        return (raw & 1 << 6) != 0;
    }

    @Override
    public int getRaw() {
        return raw;
    }
}
