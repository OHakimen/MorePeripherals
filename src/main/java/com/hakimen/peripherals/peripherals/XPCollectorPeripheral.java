package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.XPCollectorEntity;
import com.hakimen.peripherals.registry.BlockRegister;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XPCollectorPeripheral implements IPeripheral, IPeripheralProvider {

    public XPCollectorEntity tileEntity;
    @LuaFunction(mainThread = true)
    public int getCurrentXP(){
        return tileEntity.xpPoints;
    }

    @LuaFunction(mainThread = true)
    public void dumpXP(){
        tileEntity.xpPoints = 0;
        tileEntity.setChanged();
    }

    @NotNull
    @Override
    public String getType() {
        return "xp_collector";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof XPCollectorPeripheral;
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        if(world.getBlockState(pos).getBlock().equals(BlockRegister.xpCollector.get())){
            var peripheral = new XPCollectorPeripheral();
            peripheral.tileEntity = (XPCollectorEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> peripheral);
        }
        return LazyOptional.empty();
    }
}
