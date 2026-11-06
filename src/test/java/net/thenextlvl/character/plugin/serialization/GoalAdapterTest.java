package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.goal.AttackGoal;
import net.thenextlvl.character.goal.FollowGoal;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.goal.LookAtGoal;
import net.thenextlvl.character.goal.Target;
import net.thenextlvl.character.goal.WanderGoal;
import net.thenextlvl.nbt.serialization.NBT;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

// location based goals and targets need a world and are not covered here
@NullMarked
class GoalAdapterTest {
    private static final NBT NBT = net.thenextlvl.nbt.serialization.NBT.builder()
            .registerTypeHierarchyAdapter(Goal.class, new GoalAdapter())
            .registerTypeHierarchyAdapter(Target.class, new TargetAdapter())
            .build();

    static List<Goal> goals() {
        return List.of(
                new LookAtGoal(Target.viewer()),
                new LookAtGoal(Target.nearestPlayer(12), 4),
                new FollowGoal(Target.player(UUID.randomUUID())),
                new FollowGoal(Target.viewer(), 1, 20, 0.3),
                new AttackGoal(Target.viewer()),
                new AttackGoal(Target.nearestPlayer(10), 12, 1.2),
                new WanderGoal(5),
                new WanderGoal(8, 0.1)
        );
    }

    @ParameterizedTest
    @MethodSource("goals")
    void roundTrips(final Goal goal) {
        final var tag = NBT.serialize(goal, Goal.class);
        assertEquals(goal, NBT.deserialize(tag, Goal.class));
    }
}
