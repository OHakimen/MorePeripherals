package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.XPCollectorEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XPCollectorPeripheral implements IPeripheral {

    public XPCollectorEntity tileEntity;

    public XPCollectorPeripheral(XPCollectorEntity tileEntity){
        this.tileEntity = tileEntity;
    }

    @LuaFunction
    public int getCurrentXP(){
        return tileEntity.xpPoints;
    }

    @LuaFunction
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
}
