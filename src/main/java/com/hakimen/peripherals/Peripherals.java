package com.hakimen.peripherals;

import com.hakimen.peripherals.registry.*;
import com.hakimen.peripherals.utils.EnchantUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("peripherals")
public class Peripherals {

    public static CreativeModeTab tab = new CreativeModeTab("peripherals") {
        @Override
        public ItemStack makeIcon() {
            return BlockRegister.tradingInterfaceItem.get().getDefaultInstance();
        }
    };


    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String mod_id = "peripherals";
    public Peripherals() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        EnchantUtils.init();
        BlockEntityRegister.register(bus);
        BlockRegister.register(bus);
        ItemRegister.register(bus);
        ContainerRegister.register(bus);

        ComputerCraftRegister.registerPeripheralProvider();
        ComputerCraftRegister.registerTurtleUpgrades();

        bus.addListener(this::setup);
        bus.addListener(this::enqueueIMC);
        bus.addListener(this::processIMC);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(PeripheralsClient::clientInit));
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {

    }

}
