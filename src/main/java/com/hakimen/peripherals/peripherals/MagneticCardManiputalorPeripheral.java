package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.MagneticCardManiputalorEntity;
import com.hakimen.peripherals.registry.BlockRegister;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
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
        IPeripheral.super.attach(computer);
        tileEntity.computers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        IPeripheral.super.detach(computer);
        tileEntity.computers.remove(computer);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult readCard(){
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            return MethodResult.of(tileEntity.inventory.getStackInSlot(0).getOrCreateTag().getString("data"));
        } else {
            return MethodResult.of(false,"no card found");
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult writeCard(String data)  {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            tileEntity.inventory.getStackInSlot(0).getOrCreateTag().putString("data", data);
            return MethodResult.of(true);
        } else {
            return MethodResult.of(false,"no card found");
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult hasCard(){
        return MethodResult.of(!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult ejectCard(IComputerAccess computer) {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            tileEntity.getLevel().addFreshEntity(new ItemEntity(
                    tileEntity.getLevel(),
                    tileEntity.getBlockPos().getX() + 0.5,
                    tileEntity.getBlockPos().getY() + 0.5,
                    tileEntity.getBlockPos().getZ() + 0.5,
                    tileEntity.inventory.extractItem(0, 1, false)
            ));
            computer.queueEvent("card_remove");
            return MethodResult.of(true);
        }
        return MethodResult.of(false);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult setLabel(String label) {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            if (label.equals("")) {
                tileEntity.inventory.getStackInSlot(0).resetHoverName();
            } else {
                tileEntity.inventory.getStackInSlot(0).setHoverName(Component.literal(label));
            }
            return MethodResult.of(true);
        }
        return MethodResult.of(false);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getLabel()  {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            return MethodResult.of(tileEntity.inventory.getStackInSlot(0).getHoverName().getString());
        } else {
            return MethodResult.of(false,"no card found");
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult setSecure(boolean sensibility)  {
        if (!tileEntity.inventory.getStackInSlot(0).getItem().equals(Items.AIR)) {
            tileEntity.inventory.getStackInSlot(0).getOrCreateTag().putBoolean("sensible", sensibility);
            return MethodResult.of(true);
        }else{
            return MethodResult.of(false,"no card found");
        }
    }

    @Override
    public LazyOptional<IPeripheral> getPeripheral(Level world, BlockPos pos, Direction side) {
        if(world.getBlockState(pos).getBlock().equals(BlockRegister.magneticCardManipulator.get())){
            var peripheral = new MagneticCardManiputalorPeripheral();
            peripheral.tileEntity = (MagneticCardManiputalorEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> peripheral);
        }
        return LazyOptional.empty();
    }
}
