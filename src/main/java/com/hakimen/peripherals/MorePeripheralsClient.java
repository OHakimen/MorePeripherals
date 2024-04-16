package com.hakimen.peripherals;

import com.hakimen.peripherals.client.screen.*;
import com.hakimen.peripherals.client.turtle.EnderBagModeller;
import com.hakimen.peripherals.client.turtle.SolarTurtleModeller;
import com.hakimen.peripherals.registry.ComputerCraftRegister;
import com.hakimen.peripherals.registry.ContainerRegister;
import dan200.computercraft.api.client.ComputerCraftAPIClient;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


public class MorePeripheralsClient {
    public static void clientInit(FMLClientSetupEvent event){

        ComputerCraftAPIClient.registerTurtleUpgradeModeller(ComputerCraftRegister.magnet.get(), TurtleUpgradeModeller.flatItem());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(ComputerCraftRegister.enderBag.get(), new EnderBagModeller());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(ComputerCraftRegister.solar.get(), new SolarTurtleModeller());

        event.enqueueWork(() -> {
            MenuScreens.register(ContainerRegister.grinderContainer.get(), GrinderScreen::new);
            MenuScreens.register(ContainerRegister.diskRaidContainer.get(), DiskRaidScreen::new);
            MenuScreens.register(ContainerRegister.advancedDiskRaidContainer.get(), AdvancedDiskRaidScreen::new);
            MenuScreens.register(ContainerRegister.magneticCardManipulatorContainer.get(), MagneticCardManipulatorScreen::new);
            MenuScreens.register(ContainerRegister.keyboardContainer.get(), KeyboardScreen::new);
            MenuScreens.register(ContainerRegister.enderChestInterfaceContainer.get(), EnderChestInterfaceScreen::new);
            MenuScreens.register(ContainerRegister.playerInterfaceContainer.get(), PlayerInterfaceScreen::new);
            MenuScreens.register(ContainerRegister.scannerContainer.get(), ScannerScreen::new);
        });

    }

}
