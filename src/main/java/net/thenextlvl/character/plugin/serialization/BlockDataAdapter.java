package net.thenextlvl.character.plugin.serialization;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.StringTag;
import core.nbt.tag.Tag;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BlockDataAdapter implements TagAdapter<BlockData> {
    private final Server server;

    public BlockDataAdapter(Server server) {
        this.server = server;
    }

    @Override
    public BlockData deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return server.createBlockData(tag.getAsString());
    }

    @Override
    public Tag serialize(BlockData data, TagSerializationContext context) throws ParserException {
        return new StringTag(data.getAsString(true));
    }
}
