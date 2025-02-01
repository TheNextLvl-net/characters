package net.thenextlvl.character.plugin.character.action;

import net.thenextlvl.character.action.ActionType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PaperActionType<T>(String name, Class<T> type, Action<T> action) implements ActionType<T> {
}
