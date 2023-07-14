package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.items.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MorePeripherals.mod_id);
    public static DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MorePeripherals.mod_id);
    public static final RegistryObject<Item> mob_data_card = ItemRegister.ITEMS.register("spawner_card",()->{
        return new MobDataCardItem(new Item.Properties());
    });

    public static final RegistryObject<MagnetItem> magnet = ItemRegister.ITEMS.register("magnet",()->{
        return new MagnetItem(new Item.Properties().stacksTo(1));
    });
    public static final RegistryObject<MagneticCardItem> magnetic_card = ItemRegister.ITEMS.register("magnetic_card",()->{
        return new MagneticCardItem(new Item.Properties().stacksTo(1));
    });

    public static final RegistryObject<FacadeToolItem> facade_tool = ItemRegister.ITEMS.register("facade_tool",()->{
        return new FacadeToolItem(new Item.Properties().stacksTo(1));
    });

    public static final RegistryObject<KeyboardItem> keyboard = ItemRegister.ITEMS.register("keyboard",()->{
        return new KeyboardItem(new Item.Properties().stacksTo(1));
    });

    public static final RegistryObject<CreativeModeTab> tab = ItemRegister.TABS.register("main",()-> CreativeModeTab.builder()
            .icon(() -> new ItemStack(BlockRegister.tradingInterfaceItem.get()))
            .title(Component.translatable("itemGroup.peripherals"))
            .displayItems((flags, out) -> {
                ItemRegister.ITEMS.getEntries().forEach(x -> out.accept(x.get()));
            }).build());
    public static void register(IEventBus bus){
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
