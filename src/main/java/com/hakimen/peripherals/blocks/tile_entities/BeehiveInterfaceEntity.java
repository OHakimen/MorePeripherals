package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.BeehiveInterfacePeripheral;
import com.hakimen.peripherals.peripherals.EnchantingTablePeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;


public class BeehiveInterfaceEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new BeehiveInterfacePeripheral(this));

    public BeehiveInterfaceEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.beehiveInterfaceEntity.get(), pos, state);
    }

    public BlockState beehive = null;
    public BeehiveBlockEntity beehiveBlockEntity = null;

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return super.save(tag);
    }

    public void tick(){
        if(level.getBlockState(getBlockPos().above()).is(Blocks.BEEHIVE)){
            beehive = level.getBlockState(getBlockPos().above());
            beehiveBlockEntity = (BeehiveBlockEntity) level.getBlockEntity(getBlockPos().above());
            return;
        }
        else if(level.getBlockState(getBlockPos().east()).is(Blocks.BEEHIVE)){
            beehive = level.getBlockState(getBlockPos().east());
            beehiveBlockEntity = (BeehiveBlockEntity) level.getBlockEntity(getBlockPos().east());
            return;
        }
        else if(level.getBlockState(getBlockPos().west()).is(Blocks.BEEHIVE)){
            beehive = level.getBlockState(getBlockPos().west());
            beehiveBlockEntity = (BeehiveBlockEntity) level.getBlockEntity(getBlockPos().west());
            return;
        }
        else if(level.getBlockState(getBlockPos().south()).is(Blocks.BEEHIVE)){
            beehive = level.getBlockState(getBlockPos().south());
            beehiveBlockEntity = (BeehiveBlockEntity) level.getBlockEntity(getBlockPos().south());
            return;
        }
        else if(level.getBlockState(getBlockPos().north()).is(Blocks.BEEHIVE)){
            beehive = level.getBlockState(getBlockPos().north());
            beehiveBlockEntity = (BeehiveBlockEntity) level.getBlockEntity(getBlockPos().north());
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
