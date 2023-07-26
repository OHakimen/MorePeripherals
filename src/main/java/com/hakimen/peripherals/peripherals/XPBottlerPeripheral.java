package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.registry.BlockRegister;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XPBottlerPeripheral implements IPeripheral, IPeripheralProvider {

    @NotNull
    @Override
    public String getType() {
        return "xp_bottler";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof XPBottlerPeripheral;
    }

    @LuaFunction(mainThread = true)
    public MethodResult bottleXP(IComputerAccess computer, String from, String to, String xp_collector)  {

        IPeripheral inputPeripheral = computer.getAvailablePeripheral(from);
        if(inputPeripheral == null)
            return MethodResult.of(false,"the input "+from+" was not found");
        var input = extractHandler(inputPeripheral.getTarget());

        IPeripheral xpCollectorPeripheral = computer.getAvailablePeripheral(xp_collector);
        if(xpCollectorPeripheral == null)
            return MethodResult.of(false,"the xp collector "+xp_collector+" was not found");
        IPeripheral outPeripheral = computer.getAvailablePeripheral(to);
        if(outPeripheral == null)
            return MethodResult.of(false,"the output "+outPeripheral+" was not found");

        var output = extractHandler(outPeripheral.getTarget());

        var collector = (XPCollectorPeripheral)xpCollectorPeripheral;
        var xp = collector.tileEntity.xpPoints;
        int bottles = xp/8;

        if(bottles == 0){
            return MethodResult.of(false,"not enough experience in collector");
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
                    if((output.getStackInSlot(i).getCount() < output.getSlotLimit(i) && output.getStackInSlot(i).equals(Items.EXPERIENCE_BOTTLE.getDefaultInstance())) || output.getStackInSlot(i).isEmpty()){
                        output.insertItem(i,Items.EXPERIENCE_BOTTLE.getDefaultInstance(),false);
                        collector.tileEntity.xpPoints-=8;
                        collector.tileEntity.setChanged();
                        break;
                    }
                }
            }else{
                return MethodResult.of(false,"no bottles found in input");
            }
        }
        collector.tileEntity.setChanged();
        return MethodResult.of(true);
    }


    @javax.annotation.Nullable
    private static IItemHandler extractHandler(@javax.annotation.Nullable Object object) {
        if (object instanceof BlockEntity blockEntity && blockEntity.isRemoved()) return null;

        if (object instanceof ICapabilityProvider provider) {
            LazyOptional<IItemHandler> cap = provider.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (cap.isPresent()) return cap.orElseThrow(NullPointerException::new);
        }

        if (object instanceof IItemHandler handler) return handler;
        if (object instanceof Container container) return new InvWrapper(container);
        return null;
    }


    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        if(world.getBlockState(pos).getBlock().equals(BlockRegister.xpBottler.get())){
            var peripheral = new XPBottlerPeripheral();
            return LazyOptional.of(() -> peripheral);
        }
        return LazyOptional.empty();
    }
}

