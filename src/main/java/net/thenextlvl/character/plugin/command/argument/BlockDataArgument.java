package net.thenextlvl.character.plugin.command.argument;

import core.paper.command.WrappedArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

public class BlockDataArgument extends WrappedArgumentType<BlockState, BlockData> {
    public BlockDataArgument() {
        super(ArgumentTypes.blockState(), (reader, type) -> type.getBlockData());
    }
}
