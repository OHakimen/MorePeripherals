package com.hakimen.peripherals.mixin;

import dan200.computercraft.shared.peripheral.modem.wired.CableBlockEntity;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemLocalPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CableBlockEntity.class, remap = false)
public abstract class CableFacadeMixin extends BlockEntity{

    public String facade = "";

    @Shadow @Final private static String NBT_PERIPHERAL_ENABLED;

    @Shadow private boolean peripheralAccessAllowed;


    @Shadow @Final private WiredModemLocalPeripheral peripheral;

    public CableFacadeMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void load(CompoundTag nbt) {
        super.load(nbt);
        // Fallback to the previous (incorrect) key
        facade = nbt.getString("facade");
        peripheralAccessAllowed = nbt.getBoolean(NBT_PERIPHERAL_ENABLED) || nbt.getBoolean("PeirpheralAccess");
        peripheral.read(nbt, "");
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void saveAdditional(CompoundTag nbt) {
        nbt.putBoolean(NBT_PERIPHERAL_ENABLED, peripheralAccessAllowed);
        nbt.putString("facade",facade);
        peripheral.write(nbt, "");
        super.saveAdditional(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this,BlockEntity::saveWithFullMetadata);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos());
    }
}
