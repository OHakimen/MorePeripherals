package com.hakimen.peripherals.client.containers;

import com.hakimen.peripherals.registry.BlockRegister;
import com.hakimen.peripherals.registry.ContainerRegister;
import net.minecraft.core.BlockPos;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;


import javax.annotation.Nonnull;

public class DiskRaidContainer extends AbstractContainerMenu {

    private final BlockEntity blockEntity;
    private final Player playerEntity;
    private final IItemHandler playerInventory;

    public DiskRaidContainer(int windowId, BlockPos pos, Inventory playerInventory, Player player) {
        super(ContainerRegister.diskRaidContainer.get(),windowId);
        blockEntity = player.getCommandSenderWorld().getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);

        if(blockEntity != null){
            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
                addSlot(new SlotItemHandler(h,0,32,31));
                addSlot(new SlotItemHandler(h,1,56,31));
                addSlot(new SlotItemHandler(h,2,80,31));
                addSlot(new SlotItemHandler(h,3,104,31));
                addSlot(new SlotItemHandler(h,4,128,31));
            });
        }
        layoutPlayerInventorySlots(8,86);
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), playerEntity, BlockRegister.diskRaid.get());
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack( @Nonnull Player player, int index )
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            if (index < 5 && !this.moveItemStackTo(stack1, index, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            if (!this.moveItemStackTo(stack1, 0, index, false)) {
                return ItemStack.EMPTY;
            }
            if (stack1.isEmpty()) {
                slot.mayPlace(ItemStack.EMPTY);
            }
            if (stack1.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }
}
