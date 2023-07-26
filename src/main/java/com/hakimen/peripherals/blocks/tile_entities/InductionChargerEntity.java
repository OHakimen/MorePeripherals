package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.config.Config;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;


public class InductionChargerEntity extends BlockEntity {

    public EnergyStorage storage = new EnergyStorage(10240,1000,1000);
    public LazyOptional<EnergyStorage> energy = LazyOptional.of(()->storage);

    public InductionChargerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.inductionChargerEntity.get(), pos, state);
    }


    @Override
    public void load(CompoundTag tag) {
        this.storage = new EnergyStorage(10240,1000,1000, tag.getInt("Energy"));
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Energy",this.storage.getEnergyStored());
        super.saveAdditional(tag);
    }

    public void tick(){
        for (Direction dir: Direction.values()) {
            if(level.getBlockEntity(getBlockPos().relative(dir)) instanceof TurtleBlockEntity turtle)
            {
                if(turtle.getAccess().getFuelLevel() < turtle.getAccess().getFuelLimit()){
                    turtle.getAccess().addFuel((storage.extractEnergy(Config.extractRate.get(),false) * Config.conversionRate.get()));
                }
            }
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if(cap == ForgeCapabilities.ENERGY){
            return energy.cast();
        }else {
            return super.getCapability(cap,side);
        }
    }
}
