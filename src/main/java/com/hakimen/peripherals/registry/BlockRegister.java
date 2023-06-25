package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.blocks.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            MorePeripherals.mod_id);

    public static final RegistryObject<TradingInterfaceBlock> tradingInterface = BLOCKS.register("trading_interface",
            TradingInterfaceBlock::new);

    public static final RegistryObject<Item> tradingInterfaceItem = ItemRegister.ITEMS.register("trading_interface",()->{
       return new BlockItem(tradingInterface.get(),new Item.Properties());
    });

    public static final RegistryObject<XPCollectorBlock> xpCollector = BLOCKS.register("xp_collector",
            XPCollectorBlock::new);

    public static final RegistryObject<Item> xpCollectorItem = ItemRegister.ITEMS.register("xp_collector",()->{
        return new BlockItem(xpCollector.get(),new Item.Properties());
    });

    public static final RegistryObject<XPBottlerBlock> xpBottler = BLOCKS.register("xp_bottler",
            XPBottlerBlock::new);

    public static final RegistryObject<Item> xpBottlerItem = ItemRegister.ITEMS.register("xp_bottler",()->{
        return new BlockItem(xpBottler.get(),new Item.Properties());
    });



    public static final RegistryObject<GrinderBlock> grinder = BLOCKS.register("grinder",
            GrinderBlock::new);

    public static final RegistryObject<Item> grinderItem = ItemRegister.ITEMS.register("grinder",()->{
        return new BlockItem(grinder.get(),new Item.Properties());
    });


    public static final RegistryObject<DiskRaidBlock> diskRaid = BLOCKS.register("disk_raid",
            DiskRaidBlock::new);
    public static final RegistryObject<Item> diskRaidItem = ItemRegister.ITEMS.register("disk_raid",()->{
        return new BlockItem(diskRaid.get(),new Item.Properties());
    });
    public static final RegistryObject<AdvancedDiskRaidBlock> advancedDiskRaid = BLOCKS.register("advanced_disk_raid",
            AdvancedDiskRaidBlock::new);
    public static final RegistryObject<Item> advancedDiskRaidItem = ItemRegister.ITEMS.register("advanced_disk_raid",()->{
        return new BlockItem(advancedDiskRaid.get(),new Item.Properties());
    });

    public static final RegistryObject<InductionChargerBlock> inductionCharger = BLOCKS.register("induction_charger",
            InductionChargerBlock::new);
    public static final RegistryObject<Item> inductionChargerItem = ItemRegister.ITEMS.register("induction_charger",()->{
        return new BlockItem(inductionCharger.get(),new Item.Properties());
    });

    public static final RegistryObject<MagneticCardManipulatorBlock> magneticCardManipulator = BLOCKS.register("magnetic_card_manipulator",
            MagneticCardManipulatorBlock::new);
    public static final RegistryObject<Item> magneticCardManipulatorItem = ItemRegister.ITEMS.register("magnetic_card_manipulator",()->{
        return new BlockItem(magneticCardManipulator.get(),new Item.Properties());
    });


    public static final RegistryObject<BeehiveInterfaceBlock> beehiveInterface = BLOCKS.register("beehive_interface",
            BeehiveInterfaceBlock::new);

    public static final RegistryObject<Item> beehiveInterfaceItem = ItemRegister.ITEMS.register("beehive_interface",()->{
        return new BlockItem(beehiveInterface.get(), new Item.Properties());
    });
    public static final RegistryObject<SpawnerInterfaceBlock> spawnerInterfaceBlock = BLOCKS.register("spawner_interface",
            SpawnerInterfaceBlock::new);
    public static final RegistryObject<Item> spawnerInterfaceBlockItem = ItemRegister.ITEMS.register("spawner_interface",()->{
        return new BlockItem(spawnerInterfaceBlock.get(),new Item.Properties());
    });

    public static void register(IEventBus bus){
        BLOCKS.register(bus);
    }
}
