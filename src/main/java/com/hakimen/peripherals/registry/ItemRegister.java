package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.items.MagneticCardItem;
import com.hakimen.peripherals.items.MobDataCardItem;
import dan200.computercraft.shared.peripheral.modem.wired.TileCable;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MorePeripherals.mod_id);

    public static final RegistryObject<Item> mob_data_card = ItemRegister.ITEMS.register("spawner_card",()->{
        return new MobDataCardItem(new Item.Properties().tab(MorePeripherals.tab));
    });
    public static final RegistryObject<Item> magnetic_card = ItemRegister.ITEMS.register("magnetic_card",()->{
        return new MagneticCardItem(new Item.Properties().tab(MorePeripherals.tab).stacksTo(1));
    });
    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
