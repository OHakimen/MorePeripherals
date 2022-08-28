package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.items.MagneticCardItem;
import com.hakimen.peripherals.peripherals.MagneticCardManiputalorPeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import com.hakimen.peripherals.registry.ItemRegister;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;


public class MagneticCardManiputalorEntity extends BlockEntity {

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new MagneticCardManiputalorPeripheral(this));

    public final ItemStackHandler inventory = createHandler();
    public final ArrayList<IComputerAccess> computers = new ArrayList<>();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventory);


    public MagneticCardManiputalorEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.magneticCardManipulator.get(), pos, state);
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
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {

        if(cap == Capabilities.CAPABILITY_PERIPHERAL){
            return (LazyOptional<T>) peripheral;
        }else if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return (LazyOptional<T>) handler;
        }else{
            return super.getCapability(cap);
        }
    }



    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(), Block.UPDATE_ALL);
            }


            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch (slot) {
                    case 0 ->{
                        return stack.getItem() instanceof MagneticCardItem;
                    }
                }
                return false;
            }


            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                System.out.println(getStackInSlot(slot));
                if(getStackInSlot(slot).getItem().equals(ItemRegister.magnetic_card.get().asItem()) && !simulate){
                    for (IComputerAccess c: computers) {
                        c.queueEvent("card_remove");
                    }
                }
                return super.extractItem(slot, amount, simulate);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(stack.getItem().equals(ItemRegister.magnetic_card.get().asItem())){
                    for (IComputerAccess c: computers) {
                        c.queueEvent("card_insert");
                    }
                }
                super.setStackInSlot(slot, stack);
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
                setChanged();
                return super.insertItem(slot, stack, simulate);
            }
        };
    }
}
