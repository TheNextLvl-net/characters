package net.thenextlvl.character.plugin.serialization;

import com.destroystokyo.paper.profile.ProfileProperty;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ProfilePropertyAdapter implements TagAdapter<ProfileProperty> {
    @Override
    public ProfileProperty deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var name = root.get("name").getAsString();
        var value = root.get("value").getAsString();
        var signature = root.optional("signature").map(Tag::getAsString).orElse(null);
        return new ProfileProperty(name, value, signature);
    }

    @Override
    public Tag serialize(ProfileProperty property, TagSerializationContext context) throws ParserException {
        var tag = CompoundTag.builder();
        tag.put("name", property.getName());
        tag.put("value", property.getValue());
        if (property.getSignature() != null)
            tag.put("signature", property.getSignature());
        return tag.build();
    }
}
