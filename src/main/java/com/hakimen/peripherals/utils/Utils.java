package com.hakimen.peripherals.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class Utils {
    public static boolean hasAnySignal(BlockPos pos, Level level){
        return level.hasSignal(pos, Direction.DOWN) ||
                level.hasSignal(pos, Direction.UP) ||
                level.hasSignal(pos, Direction.WEST) ||
                level.hasSignal(pos, Direction.EAST) ||
                level.hasSignal(pos, Direction.NORTH) ||
                level.hasSignal(pos, Direction.SOUTH);
    }
}
