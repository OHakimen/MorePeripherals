package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.peripherals.*;
import com.hakimen.peripherals.turtleUpgrades.MagneticTurtleUpgrade;
import com.hakimen.peripherals.turtleUpgrades.SolarTurtleUpgrade;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class ComputerCraftRegister {
    public static final DeferredRegister<TurtleUpgradeSerialiser<?>> SERIALISERS = DeferredRegister.create( TurtleUpgradeSerialiser.registryId(), MorePeripherals.mod_id );

   public static final RegistryObject<TurtleUpgradeSerialiser<MagneticTurtleUpgrade>> magnet =
           SERIALISERS.register( "magnet", () -> TurtleUpgradeSerialiser.simple( MagneticTurtleUpgrade::new ) );
    public static final RegistryObject<TurtleUpgradeSerialiser<SolarTurtleUpgrade>> solar =
            SERIALISERS.register( "solar", () -> TurtleUpgradeSerialiser.simple( SolarTurtleUpgrade::new ) );
    public static void registerTurtleUpgrades(IEventBus bus){
        SERIALISERS.register(bus);
    }

    public static void registerPeripheralProvider(){

        ForgeComputerCraftAPI.registerPeripheralProvider(new AnvilPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new CrafterPeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new EnchantingTablePeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new GrindstonePeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(new LoomInterfacePeripheral());
        ForgeComputerCraftAPI.registerPeripheralProvider(((world, blockPos, direction) -> {
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
