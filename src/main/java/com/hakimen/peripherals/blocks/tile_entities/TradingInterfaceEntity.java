package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.TradingInterfacePeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import java.util.List;


public class TradingInterfaceEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new TradingInterfacePeripheral(this));

    public TradingInterfaceEntity( BlockPos pos, BlockState state) {
        super(BlockEntityRegister.tradingInterfaceEntity.get(), pos, state);
    }

    public Villager villager = null;

    @Override
    public void load(CompoundTag tag) {
        villager = (Villager) tag.get("villager");
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("villagers", (Tag) villager);
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
