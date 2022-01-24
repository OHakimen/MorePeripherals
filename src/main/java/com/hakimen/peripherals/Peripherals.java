package com.hakimen.peripherals;

import com.hakimen.peripherals.registry.BlockEntityRegister;
import com.hakimen.peripherals.registry.BlockRegister;
import com.hakimen.peripherals.registry.ContainerRegister;
import com.hakimen.peripherals.registry.ItemRegister;
import com.hakimen.peripherals.utils.EnchantUtils;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.world.Container;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
