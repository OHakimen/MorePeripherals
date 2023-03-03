package com.hakimen.peripherals.blocks.tile_entities;

import com.hakimen.peripherals.peripherals.AdvancedDiskRaidPeripheral;
import com.hakimen.peripherals.peripherals.DiskRaidPeripheral;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.MediaProviders;
import dan200.computercraft.shared.media.items.ItemDisk;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class AdvancedDiskRaidEntity extends BlockEntity {


    public final ItemStackHandler inventory = createHandler();

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new AdvancedDiskRaidPeripheral(this));
    public final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventory);

    public AdvancedDiskRaidEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegister.advancedDiskRaidEntity.get(), pos, state);
    }

    public static class MountInfo {
        public String[] mountPaths = new String[10];
    }

    public IMount[] mounts = new IMount[10];
    public final Map<IComputerAccess, MountInfo> computers = new HashMap<>();


    @Override
    public void load(CompoundTag tag) {
        this.inventory.deserializeNBT(tag);
        super.load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this,BlockEntity::saveWithFullMetadata);
    }


    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.merge(this.inventory.serializeNBT());
        super.saveAdditional(tag);
    }



    public void tick() {
        level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),Block.UPDATE_ALL);
    }


    public void mount(int i, IComputerAccess computer) {
        computers.put(computer, new MountInfo());
        mountDisk(i, computer);
    }

    public void unmount(int i, IComputerAccess computer) {
        unmountDisk(i, computer);
        computers.remove(computer);
    }

    private IMedia getDiskMedia(int i) {
        return MediaProviders.get(inventory.getStackInSlot(i));
    }

    @Override
    public void setChanged() {
        level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),Block.UPDATE_ALL);
        super.setChanged();
    }

    private synchronized void mountDisk(int i, IComputerAccess computer) {
        if (!inventory.getStackInSlot(i).isEmpty()) {
            level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),Block.UPDATE_ALL);
            MountInfo info = computers.get(computer);
            IMedia contents = getDiskMedia(i);
            if (contents != null) {
                if (mounts[i] == null) {
                    mounts[i] = contents.createDataMount(inventory.getStackInSlot(i), getLevel());
                }
                if (mounts[i] != null) {
                    if (mounts[i] instanceof IWritableMount) {
                        // Try mounting at the lowest numbered "disk" name we can
                        int n = 1;
                        while (info.mountPaths[i] == null) {
                            info.mountPaths[i] = computer.mountWritable(n == 1 ? "disk" : "disk" + n, (IWritableMount) mounts[i]);
                            n++;
                        }
                    } else {
                        // Try mounting at the lowest numbered "disk" name we can
                        int n = 1;
                        while (info.mountPaths[i] == null) {
                            info.mountPaths[i] = computer.mount(n == 1 ? "disk" : "disk" + n, mounts[i]);
                            n++;
                        }
                    }
                } else {
                    info.mountPaths[i] = null;
                }
            }
            computer.queueEvent("disk", computer.getAttachmentName());
        }
    }



    private synchronized void unmountDisk(int i, IComputerAccess computer) {
        if (!(inventory.getStackInSlot(i) == ItemStack.EMPTY)) {
            level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),Block.UPDATE_ALL);
            MountInfo info = computers.get(computer);
            if (info != null) {
                if (info.mountPaths[i] != null) {

                    computer.unmount(info.mountPaths[i]);
                    info.mountPaths[i] = null;
                }
                computer.queueEvent("disk_eject", computer.getAttachmentName());
            }
        }
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
        return new ItemStackHandler(10) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if( getLevel().isClientSide )
                {
                    super.setStackInSlot(slot, stack);
                    mounts[slot] = null;
                    setChanged();
                    return;
                }

                synchronized( this )
                {
                    if( InventoryUtil.areItemsStackable( stack, inventory.getStackInSlot(slot) ))
                    {
                        super.extractItem(slot, 1,false);
                        return;
                    }

                    // Unmount old disk
                    if( !inventory.getStackInSlot(slot).isEmpty() )
                    {
                        // TODO: Is this iteration thread safe?
                        Set<IComputerAccess> iter = computers.keySet();
                        for( IComputerAccess computer : iter ) unmountDisk(slot, computer );
                    }

                    // Swap disk over
                    super.setStackInSlot(slot, stack);
                    mounts[slot] = null;
                    setChanged();

                    // Mount new disk
                    if( !inventory.getStackInSlot(slot).isEmpty() )
                    {
                        Set<IComputerAccess> iter = computers.keySet();
                        for( IComputerAccess computer : iter ) mountDisk(slot, computer );
                    }
                }
                super.setStackInSlot(slot, stack);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof ItemDisk || stack.getItem() instanceof ItemPocketComputer;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if( !inventory.getStackInSlot(slot).isEmpty() )
                {
                    // TODO: Is this iteration thread safe?
                    Set<IComputerAccess> iter = computers.keySet();
                    for( IComputerAccess computer : iter ) unmountDisk(slot, computer );
                }

                // Swap disk over
                var stack = super.extractItem(slot, amount, simulate);
                mounts[slot] = null;
                setChanged();
                return stack;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }
}
