package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SpawnerBlock;
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
        if(level.getBlockState(getBlockPos().above()).getBlock() instanceof SpawnerBlock){
            spawner = level.getBlockState(getBlockPos().above());
            entity = (SpawnerBlockEntity) level.getBlockEntity(getBlockPos().above());
            spawners++;
        }
        if(level.getBlockState(getBlockPos().east()).getBlock() instanceof SpawnerBlock){
            spawner = level.getBlockState(getBlockPos().east());
            entity = (SpawnerBlockEntity) level.getBlockEntity(getBlockPos().east());
            spawners++;
        }
        if(level.getBlockState(getBlockPos().west()).getBlock() instanceof SpawnerBlock){
            spawner = level.getBlockState(getBlockPos().west());
            entity = (SpawnerBlockEntity) level.getBlockEntity(getBlockPos().west());
            spawners++;
        }
        if(level.getBlockState(getBlockPos().south()).getBlock() instanceof SpawnerBlock){
            spawner = level.getBlockState(getBlockPos().south());
            entity = (SpawnerBlockEntity) level.getBlockEntity(getBlockPos().south());
            spawners++;
        }
        if(level.getBlockState(getBlockPos().north()).getBlock() instanceof SpawnerBlock){
            spawner = level.getBlockState(getBlockPos().north());
            entity = (SpawnerBlockEntity) level.getBlockEntity(getBlockPos().north());
            spawners++;
        }
        if(level.getBlockState(getBlockPos().below()).getBlock() instanceof SpawnerBlock){
            spawner = level.getBlockState(getBlockPos().below());
            entity = (SpawnerBlockEntity) level.getBlockEntity(getBlockPos().below());
            spawners++;
        }
        if(spawners > 1){
            spawner = null;
            entity = null;
        }
        hasMultipleSpawners = spawners > 1;
    }
}