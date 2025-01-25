package net.thenextlvl.character;

import net.thenextlvl.character.action.ActionTypeProvider;
import net.thenextlvl.character.attribute.AttributeProvider;
import net.thenextlvl.character.skin.SkinFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CharacterProvider {
    ActionTypeProvider actionTypeProvider();

    AttributeProvider attributeProvider();

    CharacterController characterController();

    SkinFactory skinFactory();
}
