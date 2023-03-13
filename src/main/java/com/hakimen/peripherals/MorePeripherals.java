package com.hakimen.peripherals;

import com.hakimen.peripherals.client.ber.AdvancedDiskRaidRenderer;
import com.hakimen.peripherals.client.ber.DiskRaidRenderer;
import com.hakimen.peripherals.client.ber.MagneticCardManipulatorRenderer;
import com.hakimen.peripherals.config.Config;
import com.hakimen.peripherals.registry.*;
import com.hakimen.peripherals.utils.EnchantUtils;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.core.util.Colour;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.media.items.DiskItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("peripherals")
@Mod.EventBusSubscriber(modid = MorePeripherals.mod_id, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MorePeripherals {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String mod_id = "peripherals";
    public MorePeripherals() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EnchantUtils.init();
        BlockEntityRegister.register(bus);
        ItemRegister.register(bus);
        BlockRegister.register(bus);
        ContainerRegister.register(bus);
        ComputerCraftRegister.registerPeripheralProvider();
        ComputerCraftRegister.registerTurtleUpgrades();

        bus.addListener(this::setup);
        bus.addListener(this::enqueueIMC);
        bus.addListener(this::processIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonConfigSpec, "more-peripherals-common.toml");

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(MorePeripheralsClient::clientInit));
    }

    @SubscribeEvent
    public static void registerModTab(CreativeModeTabEvent.Register event){
        event.registerCreativeModeTab(new ResourceLocation(MorePeripherals.mod_id,"tab"),MorePeripherals::buildTab);
    }

    public static CreativeModeTab buildTab(CreativeModeTab.Builder builder){
        {
            return builder
                    .icon(() -> new ItemStack(BlockRegister.tradingInterfaceItem.get()))
                    .title(Component.translatable("itemGroup.peripherals"))
                    .displayItems((flags, out, isOp) -> {
                        ItemRegister.ITEMS.getEntries().forEach(x -> out.accept(x.get()));

                    }).build();
        }
    }
    @Mod.EventBusSubscriber(modid = mod_id, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(BlockEntityRegister.diskRaidEntity.get(), DiskRaidRenderer::new);
            event.registerBlockEntityRenderer(BlockEntityRegister.advancedDiskRaidEntity.get(), AdvancedDiskRaidRenderer::new);
            event.registerBlockEntityRenderer(BlockEntityRegister.magneticCardManipulator.get(), MagneticCardManipulatorRenderer::new);

        }
    }
    private void setup(final FMLCommonSetupEvent event) {

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {
    }

}
