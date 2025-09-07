package net.thenextlvl.character;

import net.thenextlvl.character.action.ActionTypeProvider;
import net.thenextlvl.character.goal.GoalFactory;
import net.thenextlvl.character.skin.SkinFactory;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CharacterProvider {
    @Contract(pure = true)
    ActionTypeProvider actionTypeProvider();

    @Contract(pure = true)
    CharacterController characterController();

    @Contract(pure = true)
    GoalFactory goalFactory();

    @Contract(pure = true)
    SkinFactory skinFactory();
}
