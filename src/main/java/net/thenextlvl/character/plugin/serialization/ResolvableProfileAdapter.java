package net.thenextlvl.character.plugin.serialization;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class ResolvableProfileAdapter implements TagAdapter<ResolvableProfile> {
    @Override
    public ResolvableProfile deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var builder = ResolvableProfile.resolvableProfile();
        root.optional("name").map(Tag::getAsString).ifPresent(builder::name);
        root.optional("uuid").map(id -> context.deserialize(id, UUID.class)).ifPresent(builder::uuid);
        root.optional("properties").map(Tag::getAsList).map(properties -> properties.stream()
                .map(property -> context.deserialize(property, ProfileProperty.class))
                .toList()
        ).ifPresent(builder::addProperties);
        return builder.build();
    }

    @Override
    public Tag serialize(ResolvableProfile profile, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.empty();
        if (profile.name() != null) tag.add("name", profile.name());
        if (profile.uuid() != null) tag.add("uuid", context.serialize(profile.uuid()));
        var properties = profile.properties().stream().map(context::serialize).toList();
        if (!properties.isEmpty()) tag.add("properties", ListTag.of(properties));
        return tag;
    }
}
