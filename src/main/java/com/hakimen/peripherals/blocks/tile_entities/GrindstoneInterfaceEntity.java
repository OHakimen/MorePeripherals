package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.GrindstonePeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;


public class GrindstoneInterfaceEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new GrindstonePeripheral(this));

    public GrindstoneInterfaceEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.grindstoneInterfaceEntity.get(), pos, state);
    }

    public BlockState grindStone = null;

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    public void tick(){
        if(level.getBlockState(getBlockPos().above()).getBlock() instanceof GrindstoneBlock){
            grindStone = level.getBlockState(getBlockPos().above());
            return;
        }
        else if(level.getBlockState(getBlockPos().east()).getBlock() instanceof GrindstoneBlock){
            grindStone = level.getBlockState(getBlockPos().east());
            return;
        }
        else if(level.getBlockState(getBlockPos().west()).getBlock() instanceof GrindstoneBlock){
            grindStone = level.getBlockState(getBlockPos().west());
            return;
        }
        else if(level.getBlockState(getBlockPos().south()).getBlock() instanceof GrindstoneBlock){
            grindStone = level.getBlockState(getBlockPos().south());
            return;
        }
        else if(level.getBlockState(getBlockPos().north()).getBlock() instanceof GrindstoneBlock){
            grindStone = level.getBlockState(getBlockPos().north());
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
