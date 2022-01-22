package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.EnchantingTablePeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;


public class EnchantingTableInterfaceEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new EnchantingTablePeripheral(this));

    public EnchantingTableInterfaceEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.enchantingInterfaceEntity.get(), pos, state);
    }

    public BlockState enchantTable = null;

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
        if(level.getBlockState(getBlockPos().above()).getBlock() instanceof EnchantmentTableBlock){
            enchantTable = level.getBlockState(getBlockPos().above());
            return;
        }
        else if(level.getBlockState(getBlockPos().east()).getBlock() instanceof EnchantmentTableBlock){
            enchantTable = level.getBlockState(getBlockPos().east());
            return;
        }
        else if(level.getBlockState(getBlockPos().west()).getBlock() instanceof EnchantmentTableBlock){
            enchantTable = level.getBlockState(getBlockPos().west());
            return;
        }
        else if(level.getBlockState(getBlockPos().south()).getBlock() instanceof EnchantmentTableBlock){
            enchantTable = level.getBlockState(getBlockPos().south());
            return;
        }
        else if(level.getBlockState(getBlockPos().north()).getBlock() instanceof EnchantmentTableBlock){
            enchantTable = level.getBlockState(getBlockPos().north());
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
