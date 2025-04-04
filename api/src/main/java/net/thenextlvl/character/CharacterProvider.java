package net.thenextlvl.character;

import net.thenextlvl.character.action.ActionTypeProvider;
import net.thenextlvl.character.goal.GoalFactory;
import net.thenextlvl.character.skin.SkinFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CharacterProvider {
    ActionTypeProvider actionTypeProvider();

    CharacterController characterController();

    GoalFactory goalFactory();

    SkinFactory skinFactory();
}
