package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.Peripherals;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegister {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Peripherals.mod_id);

    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
