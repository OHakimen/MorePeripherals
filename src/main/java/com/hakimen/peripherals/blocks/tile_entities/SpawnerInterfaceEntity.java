package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerInterfaceEntity extends BlockEntity {

    public BlockState spawner;
    public SpawnerBlockEntity entity;

    public boolean hasMultipleSpawners = false;

    public SpawnerInterfaceEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.spawnerInterfaceEntity.get(), pos, state);
    }

    public void tick(){
        int spawners = 0;
        for (Direction dir: Direction.values()) {
            if(level.getBlockState(getBlockPos().relative(dir)).is(Blocks.SPAWNER))
            {
                spawner = level.getBlockState(getBlockPos().relative(dir));
                entity = (SpawnerBlockEntity) level.getBlockEntity(getBlockPos().relative(dir));
                spawners++;
            }
        }
        if (spawners < 1){
            spawner = null;
            entity = null;
        }
        hasMultipleSpawners = spawners > 1;
    }
}