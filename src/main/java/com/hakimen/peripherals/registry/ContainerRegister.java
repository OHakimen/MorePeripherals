package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.containers.GrinderContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ContainerRegister {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MorePeripherals.mod_id);

    public static final RegistryObject<MenuType<GrinderContainer>> grinderContainer = CONTAINERS.register("grinder", ()-> IForgeMenuType.create(((windowId, inv, data) -> new GrinderContainer(windowId,data.readBlockPos(),inv,inv.player))));
    public static void register(IEventBus bus){
        CONTAINERS.register(bus);
    }
}
