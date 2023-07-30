package com.hakimen.peripherals.peripherals.turtle;

import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.platform.ForgeContainerTransfer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static dan200.computercraft.core.util.ArgumentHelpers.assertBetween;

public class EnderBagPeripheral implements IPeripheral {


    ITurtleAccess turtleAccess;
    TurtleSide side;
    public EnderBagPeripheral(ITurtleAccess turtleAccess,TurtleSide side) {
        this.turtleAccess = turtleAccess;
        this.side = side;
    }


    @Override
    public String getType() {
        return "ender_bag";
    }


    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }


    public PlayerEnderChestContainer getEnderInventory() {
        UUID ownerUUID = turtleAccess.getOwningPlayer().getId();
        Player player = turtleAccess.getLevel().getPlayerByUUID(ownerUUID);
        return player != null ? player.getEnderChestInventory() : null;
    }

    public Player getPlayer() {
        UUID ownerUUID = turtleAccess.getOwningPlayer().getId();
        Player player = turtleAccess.getLevel().getPlayerByUUID(ownerUUID);
        return player;
    }

    @LuaFunction(mainThread = true)
    public MethodResult list() {
        var inventory = getEnderInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        Map<Integer, Map<String, ?>> result = new HashMap<>();
        var size = inventory.getContainerSize();
        for (var i = 0; i < size; i++) {
            var stack = inventory.getItem(i);
            if (!stack.isEmpty()) result.put(i + 1, VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack));
        }

        return MethodResult.of(result);
    }

    @LuaFunction(mainThread = true)
    public MethodResult size() {
        var inventory = getEnderInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        return MethodResult.of(inventory.getContainerSize());
    }

    @javax.annotation.Nullable
    @LuaFunction(mainThread = true)
    public MethodResult getItemDetail(int slot) throws LuaException {
        var inventory = getEnderInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        assertBetween(slot, 1, inventory.getContainerSize(), "Slot out of range (%s)");

        var stack = inventory.getItem(slot - 1);
        return MethodResult.of(stack.isEmpty() ? false : VanillaDetailRegistries.ITEM_STACK.getDetails(stack));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getItemLimit(int slot) throws LuaException {
        var inventory = getEnderInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        assertBetween(slot, 1, inventory.getContainerSize(), "Slot out of range (%s)");
        return MethodResult.of(inventory.getItem(slot - 1).getMaxStackSize());
    }

    @LuaFunction(mainThread = true)
    public MethodResult pushItems(IComputerAccess computer, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot
    ) throws LuaException {
        var inventory = getEnderInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }

        // Find location to transfer to
        var to = turtleAccess.getInventory();

        // Validate slots
        int actualLimit = limit.orElse(Integer.MAX_VALUE);
        assertBetween(fromSlot, 1, inventory.getContainerSize(), "From slot out of range (%s)");
        if (toSlot.isPresent()) assertBetween(toSlot.get(), 1, to.getContainerSize(), "To slot out of range (%s)");

        if (actualLimit <= 0) return MethodResult.of(0);

        var og = inventory.getItem(fromSlot-1).copy();
        var extracted = inventory.removeItem(fromSlot - 1, limit.orElse(1));
        if (canAddItem(to,extracted)) {
            if (toSlot.isPresent()) {
                var itemInSlot = inventory.getItem(toSlot.get() - 1);
                boolean canPlace = false;
                ItemStack placeStack = ItemStack.EMPTY;
                if (ItemStack.isSameItem(itemInSlot, ItemStack.EMPTY)) {
                    canPlace = true;
                    placeStack = extracted;
                } else if (ItemStack.isSameItem(itemInSlot, extracted)) {
                    if (itemInSlot.getCount() + extracted.getCount() <= itemInSlot.getMaxStackSize()) {
                        canPlace = true;
                        placeStack = itemInSlot;
                        placeStack.setCount(itemInSlot.getCount() + extracted.getCount());
                    }
                }
                if (canPlace) {
                    to.setItem(toSlot.get() - 1, placeStack);
                } else {
                    return MethodResult.of(false, "Couldn't move items to slot " + toSlot.get());
                }
            } else{
                boolean canPlace = false;
                ItemStack placeStack = ItemStack.EMPTY;
                for (int i = 0; i < to.getContainerSize(); i++) {
                    var itemInSlot = to.getItem(i);
                    if (itemInSlot.isEmpty()) {
                        canPlace = true;
                        placeStack = extracted;
                    } else if (ItemStack.isSameItem(itemInSlot, extracted)) {
                        if (itemInSlot.getCount() + extracted.getCount() <= itemInSlot.getMaxStackSize()) {
                            canPlace = true;
                            placeStack = itemInSlot;
                            placeStack.setCount(itemInSlot.getCount() + extracted.getCount());
                        }
                    }
                    if (canPlace) {
                        if(placeStack != ItemStack.EMPTY){
                            to.setItem(i, placeStack);
                        }
                        break;
                    }
                }
                if(!canPlace){
                    inventory.addItem(extracted);
                }
            }
        } else {
            inventory.setItem(fromSlot - 1,og);
            return MethodResult.of(false, "Turtle Inventory is full");
        }
        return MethodResult.of(extracted.getCount());
    }

    @LuaFunction(mainThread = true)
    public MethodResult pullItems(IComputerAccess computer, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot
    ) throws LuaException {
        var inventory = getEnderInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }

        var from = turtleAccess.getInventory();

        // Validate slots
        int actualLimit = limit.orElse(Integer.MAX_VALUE);
        assertBetween(fromSlot, 1, from.getContainerSize(), "From slot out of range (%s)");
        if (toSlot.isPresent())
            assertBetween(toSlot.get(), 1, inventory.getContainerSize(), "To slot out of range (%s)");

        if (actualLimit <= 0) return MethodResult.of(0);

        var og = from.getItem(fromSlot-1).copy();
        var extracted = from.removeItem(fromSlot - 1, limit.orElse(1));

        var count = extracted.getCount();
        if (inventory.canAddItem(extracted)) {
            if (toSlot.isPresent()) {
                var itemInSlot = inventory.getItem(toSlot.get() - 1);
                boolean canPlace = false;
                ItemStack placeStack = ItemStack.EMPTY;
                if (ItemStack.isSameItem(itemInSlot, ItemStack.EMPTY)) {
                    canPlace = true;
                    placeStack = extracted;
                } else if (ItemStack.isSameItem(itemInSlot, extracted)) {
                    if (itemInSlot.getCount() + extracted.getCount() <= itemInSlot.getMaxStackSize()) {
                        canPlace = true;
                        placeStack = itemInSlot;
                        placeStack.setCount(itemInSlot.getCount() + extracted.getCount());
                    }
                }
                if (canPlace) {
                    inventory.setItem(toSlot.get() - 1, placeStack);
                } else {
                    return MethodResult.of(false, "Couldn't move items to slot " + toSlot.get());
                }
            } else
                inventory.addItem(extracted);
        } else {
            from.setItem(fromSlot - 1, og);
            return MethodResult.of(false, "Ender chest is full");
        }
        return MethodResult.of(count);
    }

    public boolean canAddItem(Container inventory, ItemStack p_19184_) {
        boolean flag = false;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            var itemstack = inventory.getItem(i);
            if (itemstack.isEmpty() || ItemStack.isSameItemSameTags(itemstack, p_19184_) && itemstack.getCount() < itemstack.getMaxStackSize()) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    @javax.annotation.Nullable
    private IItemHandler extractHandler(@javax.annotation.Nullable Object object) {
        if (object instanceof BlockEntity blockEntity && blockEntity.isRemoved()) return null;

        if (object instanceof ICapabilityProvider provider) {
            var cap = provider.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (cap.isPresent()) return cap.orElseThrow(NullPointerException::new);
        }

        if (object instanceof IItemHandler handler) return handler;
        if (object instanceof Container container) return new InvWrapper(container);
        return null;
    }

    private static int moveItem(IItemHandler from, int fromSlot, IItemHandler to, int toSlot, final int limit) {
        var fromWrapper = new ForgeContainerTransfer(from).singleSlot(fromSlot);
        var toWrapper = new ForgeContainerTransfer(to);
        if (toSlot >= 0) toWrapper = toWrapper.singleSlot(toSlot);

        return Math.max(0, fromWrapper.moveTo(toWrapper, limit));
    }
}
