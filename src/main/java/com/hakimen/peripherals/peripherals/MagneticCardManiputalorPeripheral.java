package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.MagneticCardManiputalorEntity;
import com.hakimen.peripherals.registry.BlockRegister;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagneticCardManiputalorPeripheral implements IPeripheral, IPeripheralProvider {


    private MagneticCardManiputalorEntity tileEntity;

    @NotNull
    @Override
    public String getType() {
        return "magnetic_card_manipulator";
    }


    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        tileEntity.computers.add(computer);
        IPeripheral.super.attach(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        tileEntity.computers.remove(computer);
        IPeripheral.super.detach(computer);
    }

    @LuaFunction(mainThread = true)
    public final String readCard() throws LuaException {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            return tileEntity.inventory.getStackInSlot(0).getOrCreateTag().getString("data");
        } else {
            throw new LuaException("No card found");
        }
    }

    @LuaFunction(mainThread = true)
    public final void writeCard(String data) throws LuaException {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
             tileEntity.inventory.getStackInSlot(0).getOrCreateTag().putString("data", data);
        } else {
            throw new LuaException("No card found");
        }
    }

    @LuaFunction(mainThread = true)
    public final boolean hasCard(){
        return !tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR);
    }

    @LuaFunction(mainThread = true)
    public final boolean ejectCard(IComputerAccess computer) {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            tileEntity.getLevel().addFreshEntity(new ItemEntity(
                    tileEntity.getLevel(),
                    tileEntity.getBlockPos().getX() + 0.5,
                    tileEntity.getBlockPos().getY() + 0.5,
                    tileEntity.getBlockPos().getZ() + 0.5,
                    tileEntity.inventory.extractItem(0, 1, false)
            ));
            computer.queueEvent("card_remove");
            return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean setLabel(String label) {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            if (label.equals("")) {
                tileEntity.inventory.getStackInSlot(0).resetHoverName();
            } else {
                tileEntity.inventory.getStackInSlot(0).setHoverName(Component.literal(label));
            }
            return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final String getLabel() throws LuaException {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            return tileEntity.inventory.getStackInSlot(0).getHoverName().getString();
        } else {
            throw new LuaException("No card found");
        }
    }

    @LuaFunction(mainThread = true)
    public final void setSecure(boolean sensibility) throws LuaException {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            tileEntity.inventory.getStackInSlot(0).getOrCreateTag().putBoolean("sensible", sensibility);
        }else{
            throw new LuaException("No card found");
        }
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        if(world.getBlockState(pos).getBlock().equals(BlockRegister.magneticCardManipulator.get())){
            this.tileEntity = (MagneticCardManiputalorEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> this);
        }
        return LazyOptional.empty();
    }
}
