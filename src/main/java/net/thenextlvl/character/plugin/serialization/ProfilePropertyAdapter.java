package net.thenextlvl.character.plugin.serialization;

import com.destroystokyo.paper.profile.ProfileProperty;
import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ProfilePropertyAdapter implements TagAdapter<ProfileProperty> {
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
        var tag = new CompoundTag();
        tag.add("name", property.getName());
        tag.add("value", property.getValue());
        if (property.getSignature() != null)
            tag.add("signature", property.getSignature());
        return tag;
    }
}
