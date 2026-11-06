package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.character.goal.Target;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public final class TargetAdapter implements TagAdapter<Target> {
    @Override
    public Target deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var type = root.get("type").getAsString();
        return switch (type) {
            case "viewer" -> Target.viewer();
            case "player" -> Target.player(UUID.fromString(root.get("player").getAsString()));
            case "nearest_player" -> Target.nearestPlayer(root.get("radius").getAsDouble());
            case "location" -> Target.location(context.deserialize(root.get("location"), Location.class));
            default -> throw new ParserException("Unknown target type: " + type);
        };
    }

    @Override
    public Tag serialize(final Target target, final TagSerializationContext context) throws ParserException {
        return switch (target) {
            case Target.Viewer ignored -> CompoundTag.builder().put("type", "viewer").build();
            case final Target.OfPlayer player -> CompoundTag.builder()
                    .put("type", "player")
                    .put("player", player.player().toString())
                    .build();
            case final Target.NearestPlayer nearest -> CompoundTag.builder()
                    .put("type", "nearest_player")
                    .put("radius", nearest.radius())
                    .build();
            case final Target.Position position -> CompoundTag.builder()
                    .put("type", "location")
                    .put("location", context.serialize(position.location()))
                    .build();
        };
    }
}
