package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.client.containers.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Scanner;

public class ContainerRegister {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MorePeripherals.mod_id);

    public static final RegistryObject<MenuType<GrinderContainer>> grinderContainer = CONTAINERS.register("grinder", ()-> IForgeMenuType.create(((windowId, inv, data) -> new GrinderContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<DiskRaidContainer>> diskRaidContainer = CONTAINERS.register("disk_raid", ()-> IForgeMenuType.create(((windowId, inv, data) -> new DiskRaidContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<AdvancedDiskRaidContainer>> advancedDiskRaidContainer = CONTAINERS.register("advanced_disk_raid", ()-> IForgeMenuType.create(((windowId, inv, data) -> new AdvancedDiskRaidContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<MagneticCardManipulatorContainer>> magneticCardManipulatorContainer = CONTAINERS.register("magnetic_card_manipulator", ()-> IForgeMenuType.create(((windowId, inv, data) -> new MagneticCardManipulatorContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<KeyboardContainer>> keyboardContainer = CONTAINERS.register("keyboard", ()-> IForgeMenuType.create(((windowId, inv, data) -> new KeyboardContainer(windowId))));
    public static final RegistryObject<MenuType<EnderChestInterfaceContainer>> enderChestInterfaceContainer = CONTAINERS.register("ender_chest_interface", ()-> IForgeMenuType.create(((windowId, inv, data) -> new EnderChestInterfaceContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<PlayerInterfaceContainer>> playerInterfaceContainer = CONTAINERS.register("player_interface", ()-> IForgeMenuType.create(((windowId, inv, data) -> new PlayerInterfaceContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<ScannerContainer>> scannerContainer = CONTAINERS.register("scanner", ()-> IForgeMenuType.create(((windowId, inv, data) -> new ScannerContainer(windowId,data.readBlockPos(),inv,inv.player))));


    public static void register(IEventBus bus){
        CONTAINERS.register(bus);
    }
}
