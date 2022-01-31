package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.Peripherals;
import com.hakimen.peripherals.blocks.tile_entities.*;
import com.ibm.icu.impl.CalendarCache;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES,
            Peripherals.mod_id);

    public static final RegistryObject<BlockEntityType<TradingInterfaceEntity>> tradingInterfaceEntity = BLOCK_ENTITY.register("trading_interface_entity",
            () -> BlockEntityType.Builder.of(TradingInterfaceEntity::new,BlockRegister.tradingInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<XPCollectorEntity>> xpCollectorEntity = BLOCK_ENTITY.register("xp_collector_entity",
            () -> BlockEntityType.Builder.of(XPCollectorEntity::new,BlockRegister.xpCollector.get()).build(null));

    public static final RegistryObject<BlockEntityType<XPBottlerEntity>> xpBottlerEntity = BLOCK_ENTITY.register("xp_bottler_entity",
            () -> BlockEntityType.Builder.of(XPBottlerEntity::new,BlockRegister.xpBottler.get()).build(null));

    public static final RegistryObject<BlockEntityType<EnchantingTableInterfaceEntity>> enchantingInterfaceEntity = BLOCK_ENTITY.register("enchanting_interface_entity",
            () -> BlockEntityType.Builder.of(EnchantingTableInterfaceEntity::new,BlockRegister.enchantingInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<LoomInterfaceEntity>> loomInterfaceEntity = BLOCK_ENTITY.register("loom_interface_entity",
            () -> BlockEntityType.Builder.of(LoomInterfaceEntity::new,BlockRegister.loomInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<GrinderEntity>> grinderEntity = BLOCK_ENTITY.register("grinder_entity",
            () -> BlockEntityType.Builder.of(GrinderEntity::new,BlockRegister.loomInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<GrindstoneInterfaceEntity>> grindstoneInterfaceEntity =BLOCK_ENTITY.register("grindstone_entity",
            () -> BlockEntityType.Builder.of(GrindstoneInterfaceEntity::new,BlockRegister.grindstoneInterface.get()).build(null));


    public static final RegistryObject<BlockEntityType<BeehiveInterfaceEntity>> beehiveInterfaceEntity = BLOCK_ENTITY.register("beehive_interface",
            () -> BlockEntityType.Builder.of(BeehiveInterfaceEntity::new,BlockRegister.beehiveInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<AnvilInterfaceEntity>> AnvilInterfaceEntity =BLOCK_ENTITY.register("anvil_entity",
            () -> BlockEntityType.Builder.of(AnvilInterfaceEntity::new,BlockRegister.anvilInterface.get()).build(null));



    public static void register(IEventBus bus){
        BLOCK_ENTITY.register(bus);
    }
}
