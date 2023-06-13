package com.hakimen.peripherals.peripherals.turtle;

import com.hakimen.peripherals.config.Config;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.turtle.TurtleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagnetTurtlePeripheral implements IPeripheral {

    ITurtleAccess turtleAccess;
    TurtleSide side;

    public MagnetTurtlePeripheral(ITurtleAccess turtleAccess,TurtleSide side) {
        this.turtleAccess = turtleAccess;
        this.side = side;
    }

    @Override
    public String getType() {
        return "magnet";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof MagnetTurtlePeripheral;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult magnetize(int distance) {
        if(distance <= Config.maxMagnetRange.get() && distance >= 1) {
            if(Config.magnetConsumesFuel.get())
                turtleAccess.consumeFuel(distance*distance);
            if(turtleAccess.getFuelLevel() >= distance*distance || !turtleAccess.isFuelNeeded()){
                AABB range = new AABB(turtleAccess.getPosition()).inflate(distance, 2, distance);
                List<Entity> entityList = turtleAccess.getLevel().getEntities(null, range);
                for (Entity entity : entityList) {
                    if (entity instanceof ItemEntity item) {
                        TurtleUtil.storeItemOrDrop(turtleAccess, item.getItem());
                        item.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
                turtleAccess.playAnimation(side == TurtleSide.LEFT ? TurtleAnimation.SWING_LEFT_TOOL : TurtleAnimation.SWING_RIGHT_TOOL);
                return MethodResult.of(true);
            }else{
                return MethodResult.of(false, "Not enough fuel");
            }
        }
      return MethodResult.of(false, "Invalid distance");
    }
}
