package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;


public class TradingInterfaceEntity extends BlockEntity {

    public TradingInterfaceEntity( BlockPos pos, BlockState state) {
        super(BlockEntityRegister.tradingInterfaceEntity.get(), pos, state);
    }

    public Villager villager = null;

    @Override
    public void load(CompoundTag tag) {
       super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    public void tick(){
        boolean found = false;
        List<Entity> entities = level.getEntities(null,new AABB(getBlockPos().below().north().east(2),
                getBlockPos().above(2).south(2).west(2)));
        for (Entity entity:entities){
            if(entity instanceof Villager){
                villager = (Villager) entity;
                found = true;
                break;
            }
        }
        if(!found){
            villager = null;
        }
    }
}
