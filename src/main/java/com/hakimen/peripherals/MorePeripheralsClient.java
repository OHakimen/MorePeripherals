package com.hakimen.peripherals;

import com.hakimen.peripherals.registry.ContainerRegister;
import com.hakimen.peripherals.screen.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


public class MorePeripheralsClient {
    public static void clientInit(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(ContainerRegister.grinderContainer.get(), GrinderScreen::new);
            MenuScreens.register(ContainerRegister.diskRaidContainer.get(), DiskRaidScreen::new);
            MenuScreens.register(ContainerRegister.advancedDiskRaidContainer.get(), AdvancedDiskRaidScreen::new);
            MenuScreens.register(ContainerRegister.magneticCardManipulatorContainer.get(), MagneticCardManipulatorScreen::new);
        });
    }

}
