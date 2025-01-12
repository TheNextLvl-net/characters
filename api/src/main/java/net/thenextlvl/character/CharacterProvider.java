package net.thenextlvl.character;

import com.destroystokyo.paper.SkinParts;
import net.thenextlvl.character.action.ActionTypeRegistry;
import net.thenextlvl.character.skin.SkinFactory;
import net.thenextlvl.character.skin.SkinPartBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CharacterProvider {
    ActionTypeRegistry getActionRegistry();

    SkinFactory skinFactory();

    SkinPartBuilder skinPartBuilder();

    SkinPartBuilder skinPartBuilder(SkinParts parts);

    SkinPartBuilder skinPartBuilder(int raw);
}
