package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class BeehiveInterfaceEntity extends BlockEntity {
    public BeehiveInterfaceEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.beehiveInterfaceEntity.get(), pos, state);
    }

    public BlockState beehive = null;
    public BeehiveBlockEntity beehiveBlockEntity = null;

    public boolean hasMultipleBeehives = false;

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }


    public void tick(){
        int amountOfBeehives = 0;
        for (Direction dir: Direction.values()) {
            if(level.getBlockState(getBlockPos().relative(dir)).is(Blocks.BEEHIVE))
            {
                beehive = level.getBlockState(getBlockPos().relative(dir));
                beehiveBlockEntity = (BeehiveBlockEntity) level.getBlockEntity(getBlockPos().relative(dir));
                amountOfBeehives++;
            }
        }
        if (amountOfBeehives < 1){
            beehive = null;
            beehiveBlockEntity = null;
        }

        hasMultipleBeehives = amountOfBeehives > 1;

    }
}