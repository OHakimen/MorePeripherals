package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.GrinderPeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class GrinderEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new GrinderPeripheral(this));

    public final ItemStackHandler inventory = createHandler();

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventory);


    public GrinderEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.grinderEntity.get(), pos, state);
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
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == Capabilities.CAPABILITY_PERIPHERAL) {
            return (LazyOptional<T>) peripheral;
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return (LazyOptional<T>) handler;
        }
        return super.getCapability(cap);
    }



    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch (slot) {
                    case 0 ->{
                        return stack.getItem() instanceof SwordItem;
                    }
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
