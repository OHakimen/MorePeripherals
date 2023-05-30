package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.DiskRaidEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.util.StringUtil;
import dan200.computercraft.impl.MediaProviders;
import dan200.computercraft.shared.media.items.DiskItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DiskRaidPeripheral implements IPeripheral {


    private final DiskRaidEntity tileEntity;


    public DiskRaidPeripheral(DiskRaidEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return "disk_raid";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        for (int i = 0; i < 5; i++) {
            tileEntity.mount(i, computer);
        }
        IPeripheral.super.attach(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        for (int i = 0; i < 5; i++) {
            tileEntity.unmount(i, computer);
        }
        IPeripheral.super.detach(computer);
    }


    @LuaFunction(mainThread = true)
    public final boolean isDiskPresent(int slot) {
        return !tileEntity.inventory.getStackInSlot(slot - 1).isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final Object[] getDiskLabel(int slot) {
        ItemStack stack = tileEntity.inventory.getStackInSlot(slot - 1);
        IMedia media = MediaProviders.get(stack);
        return media == null ? null : new Object[]{media.getLabel(stack)};
    }

    @LuaFunction(mainThread = true)
    public final void setDiskLabel(int slot, Optional<String> labelA) throws LuaException {
        String label = labelA.orElse(null);
        ItemStack stack = tileEntity.inventory.getStackInSlot(slot - 1);
        IMedia media = MediaProviders.get(stack);
        if (media == null) return;

        if (!media.setLabel(stack, StringUtil.normaliseLabel(label))) {
            throw new LuaException("Disk label cannot be changed");
        }
        tileEntity.inventory.extractItem(slot - 1, 1, false);
        tileEntity.inventory.insertItem(slot-1,stack,false);
    }

    @LuaFunction(mainThread = true)
    public final boolean hasData(int mount, IComputerAccess computer) {
        synchronized (this) {
            DiskRaidEntity.MountInfo info = tileEntity.computers.get(computer);
            return (info != null ? info.mountPaths[mount] : null) != null;
        }
    }

    @LuaFunction(mainThread = true)
    @javax.annotation.Nullable
    public final String getMountPath(int mount, IComputerAccess computer )
    {
        synchronized (this) {
            DiskRaidEntity.MountInfo info = tileEntity.computers.get(computer);
            return (info != null ? info.mountPaths[mount] : null);
        }
    }

    @LuaFunction(mainThread = true)
    public final void ejectDisk(int slot)
    {
        tileEntity.getLevel().addFreshEntity(new ItemEntity(tileEntity.getLevel(),
                tileEntity.getBlockPos().getX(),
                tileEntity.getBlockPos().getY(),
                tileEntity.getBlockPos().getZ(),tileEntity.inventory.extractItem(slot-1,1,false)));
    }

    @LuaFunction(mainThread = true)
    public final Object[] getDiskID(int slot)
    {
        ItemStack disk = tileEntity.inventory.getStackInSlot(slot);
        return disk.getItem() instanceof DiskItem ? new Object[] { DiskItem.getDiskID( disk ) } : null;
    }
    @javax.annotation.Nullable
    private static IItemHandler extractHandler(@javax.annotation.Nullable Object object) {
        if (object instanceof BlockEntity blockEntity && blockEntity.isRemoved()) return null;

        if (object instanceof ICapabilityProvider provider) {
            LazyOptional<IItemHandler> cap = provider.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (cap.isPresent()) return cap.orElseThrow(NullPointerException::new);
        }

        if (object instanceof IItemHandler handler) return handler;
        if (object instanceof Container container) return new InvWrapper(container);
        return null;
    }

}
