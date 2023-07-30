package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.items.PlayerCardItem;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class PlayerInterfaceEntity extends BlockEntity {


    public final ItemStackHandler inventory = createHandler();

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventory);


    public PlayerInterfaceEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.playerInterface.get(), pos, state);
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
                    return stack.getItem() instanceof PlayerCardItem;
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
