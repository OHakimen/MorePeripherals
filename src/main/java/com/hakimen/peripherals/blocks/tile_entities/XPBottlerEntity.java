package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class XPBottlerEntity extends BlockEntity {

    public XPBottlerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.xpBottlerEntity.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }
    public void tick(){

    }
}
