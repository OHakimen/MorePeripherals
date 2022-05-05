package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.LoomInterfacePeripheral;
import com.hakimen.peripherals.peripherals.TradingInterfacePeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.LoomBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class LoomInterfaceEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new LoomInterfacePeripheral(this));

    public LoomInterfaceEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.loomInterfaceEntity.get(), pos, state);
    }

    public BlockState loom;
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    public void tick(){
        if(level.getBlockState(getBlockPos().above()).getBlock() instanceof LoomBlock){
            loom = level.getBlockState(getBlockPos().above());
            return;
        }
        else if(level.getBlockState(getBlockPos().east()).getBlock() instanceof LoomBlock){
            loom = level.getBlockState(getBlockPos().east());
            return;
        }
        else if(level.getBlockState(getBlockPos().west()).getBlock() instanceof LoomBlock){
            loom = level.getBlockState(getBlockPos().west());
            return;
        }
        else if(level.getBlockState(getBlockPos().south()).getBlock() instanceof LoomBlock){
            loom = level.getBlockState(getBlockPos().south());
            return;
        }
        else if(level.getBlockState(getBlockPos().north()).getBlock() instanceof LoomBlock){
            loom = level.getBlockState(getBlockPos().north());
            return;
        }else if(level.getBlockState(getBlockPos().below()).getBlock() instanceof LoomBlock){
            loom = level.getBlockState(getBlockPos().below());
            return;
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if(cap == Capabilities.CAPABILITY_PERIPHERAL){
            return (LazyOptional<T>) peripheral;
        }else {
            return super.getCapability(cap);
        }
    }
}
