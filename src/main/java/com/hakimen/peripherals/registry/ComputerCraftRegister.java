package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.peripherals.*;
import com.hakimen.peripherals.turtleUpgrades.EnderBagUpgrade;
import com.hakimen.peripherals.turtleUpgrades.MagneticTurtleUpgrade;
import com.hakimen.peripherals.turtleUpgrades.SolarTurtleUpgrade;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class ComputerCraftRegister {
    public static final DeferredRegister<TurtleUpgradeSerialiser<?>> SERIALISERS = DeferredRegister.create( TurtleUpgradeSerialiser.registryId(), MorePeripherals.mod_id );

   public static final RegistryObject<TurtleUpgradeSerialiser<MagneticTurtleUpgrade>> magnet =
           SERIALISERS.register( "magnet", () -> TurtleUpgradeSerialiser.simple( MagneticTurtleUpgrade::new ) );
    public static final RegistryObject<TurtleUpgradeSerialiser<SolarTurtleUpgrade>> solar =
            SERIALISERS.register( "solar", () -> TurtleUpgradeSerialiser.simple( SolarTurtleUpgrade::new ) );
    public static final RegistryObject<TurtleUpgradeSerialiser<EnderBagUpgrade>> enderBag =
            SERIALISERS.register( "ender_bag", () -> TurtleUpgradeSerialiser.simple( EnderBagUpgrade::new ) );
    public static void registerTurtleUpgrades(IEventBus bus){
        SERIALISERS.register(bus);
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
        ForgeComputerCraftAPI.registerPeripheralProvider(new EnderChestInterfacePeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new PlayerInterfacePeripheral());
    }
}
