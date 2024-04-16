package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.blocks.tile_entities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Scanner;

public class BlockEntityRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,
            MorePeripherals.mod_id);

    public static final RegistryObject<BlockEntityType<TradingInterfaceEntity>> tradingInterfaceEntity = BLOCK_ENTITY.register("trading_interface_entity",
            () -> BlockEntityType.Builder.of(TradingInterfaceEntity::new,BlockRegister.tradingInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<XPCollectorEntity>> xpCollectorEntity = BLOCK_ENTITY.register("xp_collector_entity",
            () -> BlockEntityType.Builder.of(XPCollectorEntity::new,BlockRegister.xpCollector.get()).build(null));

    public static final RegistryObject<BlockEntityType<GrinderEntity>> grinderEntity = BLOCK_ENTITY.register("grinder_entity",
            () -> BlockEntityType.Builder.of(GrinderEntity::new,BlockRegister.grinder.get()).build(null));

    public static final RegistryObject<BlockEntityType<DiskRaidEntity>> diskRaidEntity = BLOCK_ENTITY.register("disk_raid_entity",
            () -> BlockEntityType.Builder.of(DiskRaidEntity::new,BlockRegister.diskRaid.get()).build(null));

    public static final RegistryObject<BlockEntityType<AdvancedDiskRaidEntity>> advancedDiskRaidEntity = BLOCK_ENTITY.register("advanced_disk_raid_entity",
            () -> BlockEntityType.Builder.of(AdvancedDiskRaidEntity::new,BlockRegister.advancedDiskRaid.get()).build(null));

    public static final RegistryObject<BlockEntityType<InductionChargerEntity>> inductionChargerEntity = BLOCK_ENTITY.register("induction_charger_entity",
            () -> BlockEntityType.Builder.of(InductionChargerEntity::new,BlockRegister.inductionCharger.get()).build(null));

    public static final RegistryObject<BlockEntityType<MagneticCardManiputalorEntity>> magneticCardManipulator = BLOCK_ENTITY.register("magnetic_card_manipulator_entity",
            () -> BlockEntityType.Builder.of(MagneticCardManiputalorEntity::new,BlockRegister.magneticCardManipulator.get()).build(null));

    public static final RegistryObject<BlockEntityType<BeehiveInterfaceEntity>> beehiveInterfaceEntity = BLOCK_ENTITY.register("beehive_interface_entity",
            () -> BlockEntityType.Builder.of(BeehiveInterfaceEntity::new,BlockRegister.beehiveInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<SpawnerInterfaceEntity>> spawnerInterfaceEntity = BLOCK_ENTITY.register("spawner_interface_entity",
            () -> BlockEntityType.Builder.of(SpawnerInterfaceEntity::new,BlockRegister.spawnerInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<EnderChestInterfaceEntity>> enderChestInterface = BLOCK_ENTITY.register("ender_chest_interface_entity",
            () -> BlockEntityType.Builder.of(EnderChestInterfaceEntity::new,BlockRegister.enderChestInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<PlayerInterfaceEntity>> playerInterface = BLOCK_ENTITY.register("player_interface_entity",
            () -> BlockEntityType.Builder.of(PlayerInterfaceEntity::new,BlockRegister.playerInterface.get()).build(null));

    public static final RegistryObject<BlockEntityType<ScannerEntity>> scanner = BLOCK_ENTITY.register("scanner_entity",
            () -> BlockEntityType.Builder.of(ScannerEntity::new,BlockRegister.scanner.get()).build(null));


    public static void register(IEventBus bus){
        BLOCK_ENTITY.register(bus);
    }
}
