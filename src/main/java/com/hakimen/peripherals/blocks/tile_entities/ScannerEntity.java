package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.items.PlayerCardItem;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.shared.media.items.PrintoutItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;


public class ScannerEntity extends BlockEntity {


    public final ItemStackHandler inventory = createHandler();

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventory);


    public ScannerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.scanner.get(), pos, state);
    }


    @Override
    public void load(CompoundTag tag) {
        this.inventory.deserializeNBT(tag);
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.merge(this.inventory.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void setChanged() {
        level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(), Block.UPDATE_ALL);
        super.setChanged();
    }
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this,BlockEntity::saveWithFullMetadata);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

    public void tick(){

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction dir) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return (LazyOptional<T>) handler;
        }
        return super.getCapability(cap,dir);
    }



    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0) {
                    return stack.getItem() instanceof PrintoutItem
                            || stack.getItem() instanceof WrittenBookItem
                            || stack.getItem() instanceof WritableBookItem;
                }
                return false;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }
}
