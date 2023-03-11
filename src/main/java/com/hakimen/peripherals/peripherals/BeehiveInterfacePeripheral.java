package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.utils.Utils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BeehiveInterfacePeripheral implements IPeripheral, IPeripheralProvider {

    BeehiveBlockEntity beehive;
    BlockState beehiveBlock;

    @NotNull
    @Override
    public String getType() {
        return "beehive_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction(mainThread = true)
    public final boolean hasBees() throws LuaException{
        return !beehive.isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final int getBeeCount() throws LuaException{
        return beehive.getOccupantCount();
    }

    @LuaFunction(mainThread = true)
    public final boolean gotFireNear() throws LuaException{
        return beehive.isFireNearby();
    }

    @LuaFunction
    public final Map<Integer,?> getBees() throws LuaException {
        if (!hasBees()) {
            throw new LuaException("there is no bees in the hive");
        }
        var bees = beehive.writeBees();
        var data = new HashMap<Integer,Map<String,Object>>();
        for (int i = 0; i < bees.size(); i++) {
            var currentBeeData = new HashMap<String,Object>();
            var bee = bees.getCompound(i);
            currentBeeData.put("TicksInHive",bee.getInt("TicksInHive"));
            currentBeeData.put("MinOccupationTicks",bee.getInt("MinOccupationTicks"));
            var entityData = bee.getCompound("EntityData");
            var currentBeeEntityData = new HashMap<String,Object>();
            currentBeeEntityData.put("HasStung",entityData.getBoolean("HasStung"));
            currentBeeEntityData.put("Health",entityData.getFloat("Health"));
            currentBeeEntityData.put("HasNectar",entityData.getBoolean("HasNectar"));
            currentBeeEntityData.put("Age",entityData.getInt("Age"));
            currentBeeEntityData.put("AngerTime",entityData.getInt("AngerTime"));

            currentBeeData.put("EntityData",currentBeeEntityData);
            data.put(i+1,currentBeeData);
        }

        return data;
    }

    @LuaFunction(mainThread = true)
    public int getHoneyLevel(){
        return beehiveBlock.getValue(BeehiveBlock.HONEY_LEVEL);
    }
    @LuaFunction(mainThread = true)
    public void collectHoney(IComputerAccess computer, String resources, String to, boolean bottled) throws LuaException {
        if(!Utils.isFromMinecraft(computer,resources)){
            throw new LuaException("this method require a vanilla inventory");
        }


        if (getHoneyLevel() < 5) {
            throw new LuaException("there is not enough honey in the hive");
        }

        IPeripheral input = computer.getAvailablePeripheral(resources);
        if (input == null) throw new LuaException("the input " + resources + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());

        IPeripheral output = computer.getAvailablePeripheral(to);
        if (output == null) throw new LuaException("the output " + to + " was not found");
        IItemHandler outputHandler = extractHandler(output.getTarget());

        var neededItem = ItemStack.EMPTY;
        var slot =-1;
        for (int i = 0; i < inputHandler.getSlots(); i++) {
            if(bottled){
                if(inputHandler.getStackInSlot(i).is(Items.GLASS_BOTTLE)){
                    neededItem = inputHandler.getStackInSlot(i);
                    slot = i;
                    break;
                }
            }else{
                if(inputHandler.getStackInSlot(i).is(Items.SHEARS)){
                    neededItem = inputHandler.getStackInSlot(i);
                    slot = i;
                    break;
                }
            }
        }
        var passed = false;
        if(neededItem == ItemStack.EMPTY){
            throw new LuaException("not found the needed item");
        }

        if(neededItem.is(Items.GLASS_BOTTLE)){
            inputHandler.extractItem(slot,1,false);
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                ItemStack outputStack = outputHandler.getStackInSlot(i);
                if(outputStack.isEmpty() || outputStack.getItem() == Items.HONEY_BOTTLE && outputStack.getCount() < outputStack.getMaxStackSize()){
                    outputHandler.insertItem(i,Items.HONEY_BOTTLE.getDefaultInstance(),false);
                    passed = true;
                    break;
                }
            }
        }else if(neededItem.is(Items.SHEARS)){
            var item = inputHandler.getStackInSlot(slot).copy();
            inputHandler.extractItem(slot,1,false);
            item.setDamageValue(item.getDamageValue()+1);
            inputHandler.insertItem(slot,item,false);
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                ItemStack outputStack = outputHandler.getStackInSlot(i);
                if(outputStack.isEmpty() || outputStack.getItem() == Items.HONEYCOMB && outputStack.getCount() < outputStack.getMaxStackSize()){
                    var stack = Items.HONEYCOMB.getDefaultInstance();
                    stack.setCount(beehive.getLevel().random.nextInt(1,4));
                    outputHandler.insertItem(i,stack,false);
                    passed = true;
                    break;
                }
            }
        }
        if(!passed){
            throw new LuaException("the destination inventory is full");
        }
        var state = beehiveBlock.setValue(BeehiveBlock.HONEY_LEVEL,0);
        var pos = beehive.getBlockPos();

        beehive.getLevel().setBlockAndUpdate(pos,state);

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
        if(world.getBlockState(pos).getBlock().equals(Blocks.BEEHIVE)||
                world.getBlockState(pos).getBlock().equals(Blocks.BEE_NEST)){

            beehive = (BeehiveBlockEntity) world.getBlockEntity(pos);
            beehiveBlock = world.getBlockState(pos);
            return LazyOptional.of(() -> this);
        }
        return LazyOptional.empty();
    }
}
