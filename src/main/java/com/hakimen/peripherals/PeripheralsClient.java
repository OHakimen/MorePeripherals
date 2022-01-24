package com.hakimen.peripherals;

import com.hakimen.peripherals.registry.ContainerRegister;
import com.hakimen.peripherals.screen.GrinderScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.awt.*;

public class PeripheralsClient {
    public static void clientInit(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(ContainerRegister.grinderContainer.get(), GrinderScreen::new);
        });
    }
}
