package com.hakimen.peripherals.client.turtle;

import com.hakimen.peripherals.registry.ItemRegister;
import com.hakimen.peripherals.turtleUpgrades.EnderBagUpgrade;
import com.mojang.math.Transformation;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class EnderBagModeller implements TurtleUpgradeModeller<EnderBagUpgrade> {
    @Override
    public TransformedModel getModel(EnderBagUpgrade upgrade, @Nullable ITurtleAccess turtle, TurtleSide side) {

        var matrix = new Matrix4f();
        matrix.set(new float[]{
                0.0f, 0.0f, -1.0f, 1.0f + (side == TurtleSide.LEFT ? -0.40625f : 0.40625f),
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, -1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
        });
        matrix.transpose();
        matrix.rotateLocalX((float)Math.toRadians(90));
        matrix.translateLocal(-1/13f,1.06125f,1/24f);
        matrix.scale(0.85f);

        return TransformedModel.of(ItemRegister.enderBag.get().getDefaultInstance(), new Transformation(matrix));
    }
}
