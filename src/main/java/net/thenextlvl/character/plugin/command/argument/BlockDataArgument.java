package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BlockDataArgument implements CustomArgumentType.Converted<BlockData, BlockState> {
    @Override
    public BlockData convert(BlockState nativeType) {
        return nativeType.getBlockData();
    }

    @Override
    public ArgumentType<BlockState> getNativeType() {
        return ArgumentTypes.blockState();
    }
}
