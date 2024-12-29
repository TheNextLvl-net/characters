package net.thenextlvl.character.skin;

import net.kyori.adventure.translation.Translatable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum SkinLayer implements Translatable {
    CAPE(0, "cape"),
    HAT(6, "hat"),
    JACKET(1, "jacket"),
    LEFT_PANTS_LEG(4, "left_pants_leg"),
    LEFT_SLEEVE(2, "left_sleeve"),
    RIGHT_PANTS_LEG(5, "right_pants_leg"),
    RIGHT_SLEEVE(3, "right_sleeve");

    private final int mask;
    private final String translationKey;

    SkinLayer(int bit, String id) {
        this.mask = 1 << bit;
        this.translationKey = "options.modelPart." + id;
    }

    public int getMask() {
        return mask;
    }

    @Override
    public String translationKey() {
        return translationKey;
    }
}
