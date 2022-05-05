package com.hakimen.peripherals;

import com.hakimen.peripherals.registry.ContainerRegister;
import com.hakimen.peripherals.screen.GrinderScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


public class MorePeripheralsClient {
    public static void clientInit(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(ContainerRegister.grinderContainer.get(), GrinderScreen::new);
        });
    }
}
