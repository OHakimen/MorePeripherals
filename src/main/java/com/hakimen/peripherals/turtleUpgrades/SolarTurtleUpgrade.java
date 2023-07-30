package com.hakimen.peripherals.turtleUpgrades;

import com.hakimen.peripherals.config.Config;
import com.hakimen.peripherals.peripherals.turtle.MagnetTurtlePeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class SolarTurtleUpgrade implements ITurtleUpgrade {

    ResourceLocation id;

    public SolarTurtleUpgrade(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.PERIPHERAL;
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new MagnetTurtlePeripheral(turtle,side);
    }

    @Override
    public ResourceLocation getUpgradeID() {
        return id;
    }

    @Override
    public String getUnlocalisedAdjective() {
        return "upgrade.peripherals.solar.adjective";
    }

    @Override
    public ItemStack getCraftingItem() {
        return Items.DAYLIGHT_DETECTOR.getDefaultInstance();
    }
    long lastTime = System.currentTimeMillis();
    @Override
    public void update(ITurtleAccess turtle, TurtleSide side) {
        var tag = turtle.getUpgradeNBTData(side);
        if(!turtle.isFuelNeeded())
            return;
        if(!turtle.getLevel().isClientSide){
            boolean isOpenToSky = turtle.getLevel().canSeeSky(turtle.getPosition());
            boolean isSunny = !turtle.getLevel().isRaining() && !turtle.getLevel().isThundering() && !turtle.getLevel().isNight();
            tag.putBoolean("collecting", true);
            if(isSunny && isOpenToSky){
                if(System.currentTimeMillis() - 1000 * Config.solarChargeRate.get()  >= lastTime) {
                    turtle.addFuel(1);
                    lastTime = System.currentTimeMillis();
                }
            }else{
                tag.putBoolean("collecting", false);
            }
            turtle.updateUpgradeNBTData(side);
        }
    }
}
