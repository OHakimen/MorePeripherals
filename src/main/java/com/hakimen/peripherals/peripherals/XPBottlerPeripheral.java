package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.XPBottlerEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.Container;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.squiddev.cobalt.Lua;

public class XPBottlerPeripheral implements IPeripheral {


    private final XPBottlerEntity tileEntity;


    public XPBottlerPeripheral(XPBottlerEntity tileEntity) {
        this.tileEntity = tileEntity;

    }

    @NotNull
    @Override
    public String getType() {
        return "xp_bottler";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof XPBottlerPeripheral;
    }

    @LuaFunction
    public boolean bottleXP(IComputerAccess computer,String from,String to,String xp_collector) throws LuaException {

        IPeripheral inputPeripheral = computer.getAvailablePeripheral(from);
        if(inputPeripheral == null) throw new LuaException("the input "+from+" was not found");
        var input = extractHandler(inputPeripheral.getTarget());

        IPeripheral xpCollectorPeripheral = computer.getAvailablePeripheral(xp_collector);
        if(xpCollectorPeripheral == null) throw new LuaException("the xp collector "+xp_collector+" was not found");
        IPeripheral outPeripheral = computer.getAvailablePeripheral(to);
        if(outPeripheral == null) throw new LuaException("the output "+outPeripheral+" was not found");

        var output = extractHandler(outPeripheral.getTarget());

        var collector = (XPCollectorPeripheral)xpCollectorPeripheral;
        var xp = collector.tileEntity.xpPoints;
        int bottles = xp/8;

        if(bottles == 0){
            return false;
        }
        for (int bottle = 0; bottle < bottles ; bottle++) {
            var hasBottle = false;
            for (int i = 0; i < input.getSlots(); i++) {
                if(input.getStackInSlot(i).getItem() == Items.GLASS_BOTTLE){
                    input.extractItem(i,1,false);
                    hasBottle = true;
                    break;
                }
            }
            if(hasBottle){
                for (int i = 0; i < output.getSlots(); i++) {
                    if((output.getStackInSlot(i).getCount() < output.getSlotLimit(i) && output.getStackInSlot(i).sameItem(Items.EXPERIENCE_BOTTLE.getDefaultInstance())) || output.getStackInSlot(i).isEmpty()){
                        output.insertItem(i,Items.EXPERIENCE_BOTTLE.getDefaultInstance(),false);
                        collector.tileEntity.xpPoints-=8;
                        collector.tileEntity.setChanged();
                        break;
                    }
                }
            }else{
                collector.tileEntity.xpPoints-=8;
                collector.tileEntity.setChanged();
                throw new LuaException("no bottles found in input");
            }
        }
        collector.tileEntity.setChanged();
        return true;
    }


    @javax.annotation.Nullable
    private static IItemHandler extractHandler( @javax.annotation.Nullable Object object )
    {
        if( object instanceof BlockEntity blockEntity && blockEntity.isRemoved() ) return null;

        if( object instanceof ICapabilityProvider provider )
        {
            LazyOptional<IItemHandler> cap = provider.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY );
            if( cap.isPresent() ) return cap.orElseThrow( NullPointerException::new );
        }

        if( object instanceof IItemHandler handler ) return handler;
        if( object instanceof Container container ) return new InvWrapper( container );
        return null;
    }


}

