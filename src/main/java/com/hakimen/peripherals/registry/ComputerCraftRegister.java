package com.hakimen.peripherals.registry;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class ComputerCraftRegister {

    public static void registerTurtleUpgrades(){

    }



    public static void registerPeripheralProvider(){
        ComputerCraftAPI.registerPeripheralProvider(((world, blockPos, direction) -> {
            BlockEntity te = world.getBlockEntity(blockPos);
            if(te == null) {
                return LazyOptional.empty();
            }
            LazyOptional<IPeripheral> capabilityLazyOptional = te.getCapability(Capabilities.CAPABILITY_PERIPHERAL);
            if(capabilityLazyOptional.isPresent()){
                return capabilityLazyOptional;
            }
            return LazyOptional.empty();
        }));
    }
}
