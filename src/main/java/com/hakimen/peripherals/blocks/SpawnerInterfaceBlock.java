package com.hakimen.peripherals.blocks;

import com.hakimen.peripherals.blocks.tile_entities.SpawnerInterfaceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SpawnerInterfaceBlock extends Block implements EntityBlock {
    public SpawnerInterfaceBlock() {
        super(Properties.copy(Blocks.DIRT).strength(2f,2f).sound(SoundType.STONE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new SpawnerInterfaceEntity(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> p_153214_) {
        return level.isClientSide ? null : ($0,$1,$2,blockEntity) -> {((SpawnerInterfaceEntity) blockEntity).tick();};
    }
}