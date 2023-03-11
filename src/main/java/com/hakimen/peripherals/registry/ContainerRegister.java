package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.client.containers.AdvancedDiskRaidContainer;
import com.hakimen.peripherals.client.containers.DiskRaidContainer;
import com.hakimen.peripherals.client.containers.GrinderContainer;
import com.hakimen.peripherals.client.containers.MagneticCardManipulatorContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ContainerRegister {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MorePeripherals.mod_id);

    public static final RegistryObject<MenuType<GrinderContainer>> grinderContainer = CONTAINERS.register("grinder", ()-> IForgeMenuType.create(((windowId, inv, data) -> new GrinderContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<DiskRaidContainer>> diskRaidContainer = CONTAINERS.register("disk_raid", ()-> IForgeMenuType.create(((windowId, inv, data) -> new DiskRaidContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<AdvancedDiskRaidContainer>> advancedDiskRaidContainer = CONTAINERS.register("advanced_disk_raid", ()-> IForgeMenuType.create(((windowId, inv, data) -> new AdvancedDiskRaidContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static final RegistryObject<MenuType<MagneticCardManipulatorContainer>> magneticCardManipulatorContainer = CONTAINERS.register("magnetic_card_manipulator", ()-> IForgeMenuType.create(((windowId, inv, data) -> new MagneticCardManipulatorContainer(windowId,data.readBlockPos(),inv,inv.player))));

    public static void register(IEventBus bus){
        CONTAINERS.register(bus);
    }
}
