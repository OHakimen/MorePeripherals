package com.hakimen.peripherals.client.turtle;

import com.hakimen.peripherals.turtleUpgrades.SolarTurtleUpgrade;
import com.mojang.math.Transformation;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SolarTurtleModeller implements TurtleUpgradeModeller<SolarTurtleUpgrade> {
    @Override
    public TransformedModel getModel(SolarTurtleUpgrade upgrade, @Nullable ITurtleAccess turtle, TurtleSide side) {
        if(turtle == null){
            Transformation transformation = new Transformation(
                    new Vector3f(0.175f,0.65f,0.145f),
                    new Quaternionf(1,1,1,0),
                    new Vector3f(.0725f,.0725f,.0725f),
                    new Quaternionf(1,1,1,0));
            return TransformedModel.of(Items.DAYLIGHT_DETECTOR.getDefaultInstance(), transformation);
        }else{
            var nbt = turtle.getUpgradeNBTData(side);
            Transformation transformation = new Transformation(
                    new Vector3f(0.175f,0.65f + (nbt.getBoolean("collecting") ? 0.10f : 0),0.145f),
                    new Quaternionf(1,1,1,0),
                    new Vector3f(.0725f,.0725f,.0725f),
                    new Quaternionf(1,1,1,0));
            return TransformedModel.of(Items.DAYLIGHT_DETECTOR.getDefaultInstance(), transformation);
        }
    }
}
