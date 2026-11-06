package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.goal.AttackGoal;
import net.thenextlvl.character.goal.FollowGoal;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.goal.LookAtGoal;
import net.thenextlvl.character.goal.PatrolGoal;
import net.thenextlvl.character.goal.Target;
import net.thenextlvl.character.goal.WanderGoal;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GoalAdapter implements TagAdapter<Goal> {
    @Override
    public Goal deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var type = root.get("type").getAsString();
        return switch (type) {
            case "look_at" -> new LookAtGoal(
                    context.deserialize(root.get("target"), Target.class),
                    root.get("range").getAsDouble()
            );
            case "follow" -> new FollowGoal(
                    context.deserialize(root.get("target"), Target.class),
                    root.get("distance").getAsDouble(),
                    root.get("range").getAsDouble(),
                    root.get("speed").getAsDouble()
            );
            case "attack" -> new AttackGoal(
                    context.deserialize(root.get("target"), Target.class),
                    root.get("range").getAsDouble(),
                    root.get("speed").getAsDouble()
            );
            case "wander" -> new WanderGoal(
                    root.get("radius").getAsDouble(),
                    root.get("speed").getAsDouble()
            );
            case "patrol" -> new PatrolGoal(
                    root.<ListTag<Tag>>getAsList("points").stream()
                            .map(point -> context.deserialize(point, Location.class))
                            .toList(),
                    root.get("speed").getAsDouble()
            );
            default -> throw new ParserException("Unknown goal type: " + type);
        };
    }

    @Override
    public Tag serialize(final Goal goal, final TagSerializationContext context) throws ParserException {
        return switch (goal) {
            case final LookAtGoal lookAt -> CompoundTag.builder()
                    .put("type", "look_at")
                    .put("target", context.serialize(lookAt.target(), Target.class))
                    .put("range", lookAt.range())
                    .build();
            case final FollowGoal follow -> CompoundTag.builder()
                    .put("type", "follow")
                    .put("target", context.serialize(follow.target(), Target.class))
                    .put("distance", follow.distance())
                    .put("range", follow.range())
                    .put("speed", follow.speed())
                    .build();
            case final AttackGoal attack -> CompoundTag.builder()
                    .put("type", "attack")
                    .put("target", context.serialize(attack.target(), Target.class))
                    .put("range", attack.range())
                    .put("speed", attack.speed())
                    .build();
            case final WanderGoal wander -> CompoundTag.builder()
                    .put("type", "wander")
                    .put("radius", wander.radius())
                    .put("speed", wander.speed())
                    .build();
            case final PatrolGoal patrol -> CompoundTag.builder()
                    .put("type", "patrol")
                    .put("points", ListTag.of(patrol.points().stream().map(context::serialize).toList()))
                    .put("speed", patrol.speed())
                    .build();
        };
    }
}
