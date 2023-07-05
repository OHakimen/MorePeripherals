package com.hakimen.peripherals.mixin;

import com.hakimen.peripherals.blocks.FacadedBlockEntity;
import com.hakimen.peripherals.utils.NBTUtils;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockEntity;
import dan200.computercraft.shared.util.BlockEntityHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CableBlockEntity.class)
abstract class CableFacadeMixin extends BlockEntity implements FacadedBlockEntity {
    public BlockState facade = Blocks.AIR.defaultBlockState();

    public CableFacadeMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "load", at = @At("HEAD"))
    public void load(CompoundTag nbt, CallbackInfo ci) {
        facade = NBTUtils.readBlockState(nbt.getCompound("facade"));
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    public void saveAdditional(CompoundTag nbt, CallbackInfo ci) {
        nbt.put("facade", NbtUtils.writeBlockState(facade));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        var t = super.getUpdateTag();
        t.put("facade", NbtUtils.writeBlockState(facade));
        return t;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) handleUpdateTag(tag);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        facade = NBTUtils.readBlockState(tag.getCompound("facade"));
        requestModelDataUpdate();
        BlockEntityHelpers.updateBlock(this);
    }

    @Override
    public BlockState getFacade() {
        return facade;
    }

    @Override
    public void setFacade(BlockState state) {
        facade = state;
        BlockEntityHelpers.updateBlock(this);
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(FacadedBlockEntity.PROPERTY, facade).build();
    }
}
