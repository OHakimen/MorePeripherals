package com.hakimen.peripherals;

import com.hakimen.peripherals.client.screen.AdvancedDiskRaidScreen;
import com.hakimen.peripherals.client.screen.DiskRaidScreen;
import com.hakimen.peripherals.client.screen.GrinderScreen;
import com.hakimen.peripherals.client.screen.MagneticCardManipulatorScreen;
import com.hakimen.peripherals.registry.ComputerCraftRegister;
import com.hakimen.peripherals.registry.ContainerRegister;
import dan200.computercraft.api.client.ComputerCraftAPIClient;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


public class MorePeripheralsClient {
    public static void clientInit(FMLClientSetupEvent event){

        ComputerCraftAPIClient.registerTurtleUpgradeModeller(ComputerCraftRegister.magnet.get(), TurtleUpgradeModeller.flatItem());

        event.enqueueWork(() -> {
            MenuScreens.register(ContainerRegister.grinderContainer.get(), GrinderScreen::new);
            MenuScreens.register(ContainerRegister.diskRaidContainer.get(), DiskRaidScreen::new);
            MenuScreens.register(ContainerRegister.advancedDiskRaidContainer.get(), AdvancedDiskRaidScreen::new);
            MenuScreens.register(ContainerRegister.magneticCardManipulatorContainer.get(), MagneticCardManipulatorScreen::new);

        });


    }

}
