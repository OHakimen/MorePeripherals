package com.hakimen.peripherals.containers;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class CrafterContainer extends CraftingContainer {
    SimpleContainer container = new SimpleContainer(9);
    public CrafterContainer() {
        super(null,0,0);
    }


    @Override
    public void setItem(int i, ItemStack stack) {
        container.setItem(i,stack);
    }

    @Override
    public void clearContent() {
        container.clearContent();
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return container.removeItem(i,j);
    }

    @Override
    public ItemStack getItem(int p_39332_) {
        return container.getItem(p_39332_);
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public int countItem(Item p_18948_) {
        return container.countItem(p_18948_);
    }

    @Override
    public boolean canPlaceItem(int p_18952_, ItemStack p_18953_) {
        return container.canPlaceItem(p_18952_, p_18953_);
    }

    @Override
    public boolean hasAnyOf(Set<Item> p_18950_) {
        return container.hasAnyOf(p_18950_);
    }

    @Override
    public int getContainerSize() {
        return container.getContainerSize();
    }

    @Override
    public boolean stillValid(Player p_39340_) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    @Override
    public void fillStackedContents(StackedContents p_39342_) {
        container.fillStackedContents(p_39342_);
    }

    @Override
    public int getMaxStackSize() {
        return container.getMaxStackSize();
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_39344_) {
        return container.removeItemNoUpdate(p_39344_);
    }


}
