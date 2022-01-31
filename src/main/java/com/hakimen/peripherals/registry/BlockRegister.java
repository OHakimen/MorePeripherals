package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.Peripherals;
import com.hakimen.peripherals.blocks.*;
import com.hakimen.peripherals.peripherals.GrindstonePeripheral;
import com.ibm.icu.impl.CalendarCache;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lwjgl.system.CallbackI;

public class BlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            Peripherals.mod_id);

    public static final RegistryObject<TradingInterfaceBlock> tradingInterface = BLOCKS.register("trading_interface",
            TradingInterfaceBlock::new);

    public static final RegistryObject<Item> tradingInterfaceItem = ItemRegister.ITEMS.register("trading_interface",()->{
       return new BlockItem(tradingInterface.get(),new Item.Properties().tab(Peripherals.tab));
    });

    public static final RegistryObject<XPCollectorBlock> xpCollector = BLOCKS.register("xp_collector",
            XPCollectorBlock::new);

    public static final RegistryObject<Item> xpCollectorItem = ItemRegister.ITEMS.register("xp_collector",()->{
        return new BlockItem(xpCollector.get(),new Item.Properties().tab(Peripherals.tab));
    });

    public static final RegistryObject<XPBottlerBlock> xpBottler = BLOCKS.register("xp_bottler",
            XPBottlerBlock::new);

    public static final RegistryObject<Item> xpBottlerItem = ItemRegister.ITEMS.register("xp_bottler",()->{
        return new BlockItem(xpBottler.get(),new Item.Properties().tab(Peripherals.tab));
    });


    public static final RegistryObject<EnchantingTableInterfaceBlock> enchantingInterface = BLOCKS.register("enchanting_interface",
            EnchantingTableInterfaceBlock::new);

    public static final RegistryObject<Item> enchantingInterfaceItem = ItemRegister.ITEMS.register("enchanting_interface",()->{
        return new BlockItem(enchantingInterface.get(),new Item.Properties().tab(Peripherals.tab));
    });


    public static final RegistryObject<LoomInterfaceBlock> loomInterface = BLOCKS.register("loom_interface",
            LoomInterfaceBlock::new);

    public static final RegistryObject<Item> loomInterfaceItem = ItemRegister.ITEMS.register("loom_interface",()->{
        return new BlockItem(loomInterface.get(),new Item.Properties().tab(Peripherals.tab));
    });

    public static final RegistryObject<GrinderBlock> grinder = BLOCKS.register("grinder",
            GrinderBlock::new);

    public static final RegistryObject<Item> grinderItem = ItemRegister.ITEMS.register("grinder",()->{
        return new BlockItem(grinder.get(),new Item.Properties().tab(Peripherals.tab));
    });

    public static final RegistryObject<GrindstoneInterfaceBlock> grindstoneInterface = BLOCKS.register("grindstone_interface",
            GrindstoneInterfaceBlock::new);

    public static final RegistryObject<Item> grindstoneInterfaceItem = ItemRegister.ITEMS.register("grindstone_interface",()->{
        return new BlockItem(grindstoneInterface.get(),new Item.Properties().tab(Peripherals.tab));
    });
    public static final RegistryObject<AnvilInterfaceBlock> anvilInterface = BLOCKS.register("anvil_interface",
            AnvilInterfaceBlock::new);

    public static final RegistryObject<Item> anvilItem = ItemRegister.ITEMS.register("anvil_interface",()->{
        return new BlockItem(anvilInterface.get(),new Item.Properties().tab(Peripherals.tab));
    });

    public static final RegistryObject<BeehiveInterfaceBlock> beehiveInterface = BLOCKS.register("beehive_interface",
            BeehiveInterfaceBlock::new);

    public static final RegistryObject<Item> beehiveInterfaceItem = ItemRegister.ITEMS.register("beehive_interface",()->{
        return new BlockItem(beehiveInterface.get(),new Item.Properties().tab(Peripherals.tab));
    });


    public static void register(IEventBus bus){
        BLOCKS.register(bus);
    }
}
