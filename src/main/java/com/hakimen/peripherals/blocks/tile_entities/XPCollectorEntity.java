package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.XPCollectorPeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.computer.Computer;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class XPCollectorEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new XPCollectorPeripheral(this));

    public int xpPoints;

    public XPCollectorEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityRegister.xpCollectorEntity.get(), p_155229_, p_155230_);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("StoredXP",xpPoints);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        xpPoints = tag.getInt("StoredXP");
        super.load(tag);
    }


    public void tick(){
        List<Entity> entities = level.getEntities(null,new AABB(getBlockPos().below().north().east(2),
                getBlockPos().above(2).south(2).west(2)));
        for (Entity entity:entities){
            if(entity instanceof ExperienceOrb){
                setChanged();
                xpPoints += ((ExperienceOrb) entity).getValue();
                entity.discard();
            }
        }
   }

    @Override
    public void setChanged() {
        super.setChanged();
        getLevel().sendBlockUpdated( getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL );
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
