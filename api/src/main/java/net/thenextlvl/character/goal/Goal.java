package net.thenextlvl.character.goal;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Goal {
    boolean isRunning();

    void start();

    void cancel();

    interface Builder<T extends Goal> {
        T build();
    }
}
