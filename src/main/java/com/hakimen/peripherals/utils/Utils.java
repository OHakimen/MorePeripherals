package com.hakimen.peripherals.utils;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class Utils {
    public static boolean isFromMinecraft(IComputerAccess computer,String from){
        IPeripheral inputLocation = computer.getAvailablePeripheral(from);
        return inputLocation.getType().contains("minecraft");
    }

    public static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
        if (stack1.getItem() != stack2.getItem()) return false;
        if (stack1.getDamageValue() != stack2.getDamageValue()) return false;
        if (stack1.getCount() > stack1.getMaxStackSize()) return false;
        return ItemStack.tagMatches(stack1, stack2);
    }
}
