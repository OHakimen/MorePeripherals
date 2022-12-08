package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.peripherals.CrafterPeripheral;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import oshi.util.tuples.Pair;

import java.util.ArrayList;


public class ComputerCraftRegister {


    public static void registerTurtleUpgrades(){
    }



    public static void registerPeripheralProvider(){
        ComputerCraftAPI.registerPeripheralProvider(new CrafterPeripheral());
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
