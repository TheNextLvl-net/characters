package net.thenextlvl.character.skin;

import net.thenextlvl.character.plugin.character.PaperSkinPartBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SkinTest {
    private final PaperSkinPartBuilder builder = new PaperSkinPartBuilder();

    @Test
    @DisplayName("skin part capes")
    public void testCape() {
        Assertions.assertTrue(builder.build().hasCapeEnabled());
        builder.cape(false);
        Assertions.assertFalse(builder.build().hasCapeEnabled());
    }

    @Test
    @DisplayName("skin part hat")
    public void testHat() {
        Assertions.assertTrue(builder.build().hasHatsEnabled());
        builder.hat(false);
        Assertions.assertFalse(builder.build().hasHatsEnabled());
    }

    @Test
    @DisplayName("skin part jacket")
    public void testJacket() {
        Assertions.assertTrue(builder.build().hasJacketEnabled());
        builder.jacket(false);
        Assertions.assertFalse(builder.build().hasJacketEnabled());
    }

    @Test
    @DisplayName("skin part left pants")
    public void testLeftPants() {
        Assertions.assertTrue(builder.build().hasLeftPantsEnabled());
        builder.leftPants(false);
        Assertions.assertFalse(builder.build().hasLeftPantsEnabled());
    }

    @Test
    @DisplayName("skin part left sleeve")
    public void testLeftSleeve() {
        Assertions.assertTrue(builder.build().hasLeftSleeveEnabled());
        builder.leftSleeve(false);
        Assertions.assertFalse(builder.build().hasLeftSleeveEnabled());
    }

    @Test
    @DisplayName("skin part right pants")
    public void testRightPants() {
        Assertions.assertTrue(builder.build().hasRightPantsEnabled());
        builder.rightPants(false);
        Assertions.assertFalse(builder.build().hasRightPantsEnabled());
    }

    @Test
    @DisplayName("skin part right sleeve")
    public void testRightSleeve() {
        Assertions.assertTrue(builder.build().hasRightSleeveEnabled());
        builder.rightSleeve(false);
        Assertions.assertFalse(builder.build().hasRightSleeveEnabled());
    }
}
