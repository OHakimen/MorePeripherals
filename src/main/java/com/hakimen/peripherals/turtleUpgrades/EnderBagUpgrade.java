package com.hakimen.peripherals.turtleUpgrades;

import com.hakimen.peripherals.peripherals.turtle.EnderBagPeripheral;
import com.hakimen.peripherals.registry.ItemRegister;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EnderBagUpgrade implements ITurtleUpgrade {

    ResourceLocation id;

    public EnderBagUpgrade(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.PERIPHERAL;
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new EnderBagPeripheral(turtle,side);
    }

    @Override
    public ResourceLocation getUpgradeID() {
        return id;
    }

    @Override
    public String getUnlocalisedAdjective() {
        return "upgrade.peripherals.ender_bag.adjective";
    }

    @Override
    public ItemStack getCraftingItem() {
        return ItemRegister.enderBag.get().getDefaultInstance();
    }
    @Override
    public void update(ITurtleAccess turtle, TurtleSide side) {

    }
}
