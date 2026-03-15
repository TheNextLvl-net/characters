package net.thenextlvl.character.plugin.serialization;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class LocationAdapter implements TagAdapter<Location> {
    @Override
    public Location deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var root = tag.getAsCompound();
        final var x = root.get("x").getAsDouble();
        final var y = root.get("y").getAsDouble();
        final var z = root.get("z").getAsDouble();
        final var yaw = root.get("yaw").getAsFloat();
        final var pitch = root.get("pitch").getAsFloat();
        final var world = context.deserialize(root.get("world"), World.class);
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public Tag serialize(final Location location, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("x", location.getX())
                .put("y", location.getY())
                .put("z", location.getZ())
                .put("yaw", location.getYaw())
                .put("pitch", location.getPitch())
                .put("world", context.serialize(location.getWorld()))
                .build();
    }
}
