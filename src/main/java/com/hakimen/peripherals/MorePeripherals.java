package com.hakimen.peripherals;

import com.hakimen.peripherals.blocks.FacadedBlockEntity;
import com.hakimen.peripherals.client.ber.AdvancedDiskRaidRenderer;
import com.hakimen.peripherals.client.ber.DiskRaidRenderer;
import com.hakimen.peripherals.client.ber.MagneticCardManipulatorRenderer;
import com.hakimen.peripherals.client.model.CableBakedModel;
import com.hakimen.peripherals.config.Config;
import com.hakimen.peripherals.registry.*;
import com.hakimen.peripherals.utils.EnchantUtils;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
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
        ComputerCraftRegister.registerTurtleUpgrades(bus);
        ComputerCraftRegister.registerPeripheralProvider();
        PacketRegister.register();

        bus.addListener(this::setup);
        bus.addListener(this::enqueueIMC);
        bus.addListener(this::processIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonConfigSpec, "more-peripherals-common.toml");

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(MorePeripheralsClient::clientInit));
    }

    @Mod.EventBusSubscriber(modid = mod_id, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

        @SubscribeEvent
        public static void onModifyBakingEvent(ModelEvent.ModifyBakingResult event) {
            for (var entry : event.getModels().entrySet()) {
                if (entry.getKey() instanceof ModelResourceLocation location && location.getNamespace().equals(ComputerCraftAPI.MOD_ID) && location.getPath().equals("cable")) {
                    event.getModels().put(entry.getKey(), new CableBakedModel(entry.getValue()));
                }
            }
        }

        @SubscribeEvent
        public static void registerColours(RegisterColorHandlersEvent.Block event) {
            event.register((state, level, pos, layer) ->
                            level != null && pos != null && level.getBlockEntity(pos) instanceof FacadedBlockEntity facaded && !facaded.getFacade().isAir()
                                    ? Minecraft.getInstance().getBlockColors().getColor(facaded.getFacade(), level, pos, layer)
                                    : -1,
                    ModRegistry.Blocks.CABLE.get()
            );
        }

        @SubscribeEvent
        public static void registerColours(RegisterColorHandlersEvent.Item event) {
            event.register((stack, layer) ->
                    layer == 1 ?
                            -1 :
                            (ItemRegister.magnetic_card.get().getColor(stack) == 10511680 ?
                                    -1 :
                                    ItemRegister.magnetic_card.get().getColor(stack))
                    , ItemRegister.magnetic_card.get());
        }
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {
    }

}
