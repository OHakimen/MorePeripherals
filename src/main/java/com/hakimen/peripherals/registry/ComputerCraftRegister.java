package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.peripherals.*;
import dan200.computercraft.api.ForgeComputerCraftAPI;


public class ComputerCraftRegister {


    public static void registerTurtleUpgrades(){
    }



    public static void registerPeripheralProvider(){
        ForgeComputerCraftAPI.registerPeripheralProvider(new AnvilPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new CrafterPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new EnchantingTablePeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new GrindstonePeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new LoomInterfacePeripheral());

        ForgeComputerCraftAPI.registerPeripheralProvider(new XPCollectorPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new XPBottlerPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new TradingInterfacePeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new SpawnerPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new MagneticCardManiputalorPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new GrinderPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new DiskRaidPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new BeehiveInterfacePeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new AdvancedDiskRaidPeripheral());
    }
}
