package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.BeehiveInterfaceEntity;
import com.hakimen.peripherals.registry.BlockRegister;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BeehiveInterfacePeripheral implements IPeripheral, IPeripheralProvider {

    BeehiveInterfaceEntity tileEntity;


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
    public final MethodResult hasBees(){
        if(hasMultipleBeehives()) {
            return MethodResult.of(false,"more than one beehive connected");
        }
        if(!hasBeehive()){
            return MethodResult.of(false,"no beehive present");
        }
        return MethodResult.of(!tileEntity.beehiveBlockEntity.isEmpty());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getBeeCount(){
        if(hasMultipleBeehives()) {
            return MethodResult.of(false,"more than one beehive connected");
        }
        if(!hasBeehive()){
            return MethodResult.of(false,"no beehive present");
        }

        return  MethodResult.of(true,tileEntity.beehiveBlockEntity.getOccupantCount());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult gotFireNear(){
        if(hasMultipleBeehives()) {
            return MethodResult.of(false,"more than one beehive connected");
        }
        if(!hasBeehive()){
            return MethodResult.of(false,"no beehive present");
        }

        return MethodResult.of(true,tileEntity.beehiveBlockEntity.isFireNearby());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getBees(){
        if(hasMultipleBeehives()) {
            return MethodResult.of(false,"more than one beehive connected");
        }
        if(!hasBeehive()){
            return MethodResult.of(false,"no beehive present");
        }


        if (tileEntity.beehiveBlockEntity.isEmpty()) {
            return MethodResult.of(false,"there is no bees in the hive");
        }
        var bees = tileEntity.beehiveBlockEntity.writeBees();
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

        return MethodResult.of(true,data);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getHoneyLevel() {
        if(hasMultipleBeehives()) {
            return MethodResult.of(false,"more than one beehive connected");
        }
        if(!hasBeehive()){
            return MethodResult.of(false,"no beehive present");
        }

        return MethodResult.of(true,tileEntity.beehive.getValue(BeehiveBlock.HONEY_LEVEL));
    }
    @LuaFunction(mainThread = true)
    public MethodResult collectHoney(IComputerAccess computer, String resources, String to, boolean bottled) throws LuaException {
        if(hasMultipleBeehives()) {
            return MethodResult.of(false,"more than one beehive connected");
        }
        if(!hasBeehive()){
            return MethodResult.of(false,"no beehive present");
        }

        if (tileEntity.beehive.getValue(BeehiveBlock.HONEY_LEVEL) < 5) {
            return MethodResult.of(false,"there is not enough honey in the hive");
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
            return MethodResult.of(false,"not found the needed item");
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
            if(item.getDamageValue() < item.getMaxDamage()){
                inputHandler.insertItem(slot,item,false);
            }
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                ItemStack outputStack = outputHandler.getStackInSlot(i);
                if(outputStack.isEmpty() || outputStack.getItem() == Items.HONEYCOMB && outputStack.getCount() < outputStack.getMaxStackSize()){
                    var stack = Items.HONEYCOMB.getDefaultInstance();
                    stack.setCount(tileEntity.getLevel().random.nextInt(1,4));
                    outputHandler.insertItem(i,stack,false);
                    passed = true;
                    break;
                }
            }
        }
        if(!passed){
            return MethodResult.of(false,"the destination inventory is full");
        }
        var state = tileEntity.beehive.setValue(BeehiveBlock.HONEY_LEVEL,0);
        var pos = tileEntity.beehiveBlockEntity.getBlockPos();

        tileEntity.getLevel().setBlockAndUpdate(pos,state);
        return MethodResult.of(true);
    }

    public boolean hasBeehive(){
        return tileEntity.beehiveBlockEntity != null;
    }

    public boolean hasMultipleBeehives() {
        return tileEntity.hasMultipleBeehives;
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



    @Override
    public LazyOptional<IPeripheral> getPeripheral(Level world, BlockPos pos, Direction side) {
        if(world.getBlockState(pos).getBlock().equals(BlockRegister.beehiveInterface.get())){
            var peripheral = new BeehiveInterfacePeripheral();
            peripheral.tileEntity = (BeehiveInterfaceEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> peripheral);
        }
        return LazyOptional.empty();
    }
}
