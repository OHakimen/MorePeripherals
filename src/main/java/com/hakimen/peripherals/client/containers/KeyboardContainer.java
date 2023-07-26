package com.hakimen.peripherals.client.containers;

import com.hakimen.peripherals.registry.ContainerRegister;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class KeyboardContainer extends AbstractContainerMenu {


    public KeyboardContainer(int windowId) {
        super(ContainerRegister.keyboardContainer.get(),windowId);
    }


    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

}
