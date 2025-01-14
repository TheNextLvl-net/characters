package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

@NullMarked
public class AddressAdapter implements TagAdapter<InetSocketAddress> {
    @Override
    public InetSocketAddress deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var root = tag.getAsCompound();
        var hostname = root.get("hostname").getAsString();
        var port = root.get("port").getAsInt();
        return new InetSocketAddress(hostname, port);
    }

    @Override
    public Tag serialize(InetSocketAddress address, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("hostname", address.getHostName());
        tag.add("port", address.getPort());
        return tag;
    }
}
