package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.XPBottlerPeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.turtle.upgrades.CraftingTablePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class XPBottlerEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new XPBottlerPeripheral(this));

    public XPBottlerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.xpBottlerEntity.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }
    public void tick(){

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
