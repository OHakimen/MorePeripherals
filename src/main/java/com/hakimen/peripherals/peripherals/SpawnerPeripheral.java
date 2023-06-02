package com.hakimen.peripherals.peripherals;

import cc.tweaked.internal.cobalt.LuaThread;
import com.hakimen.peripherals.items.MobDataCardItem;
import com.hakimen.peripherals.utils.Utils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SpawnerPeripheral implements IPeripheral, IPeripheralProvider {

    SpawnerBlockEntity entity;
    long lastTime;
    @NotNull
    @Override
    public String getType() {
        return "spawner_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction(mainThread = true)
    public final boolean changeSpawner(IComputerAccess computer, String inv, int slot, Optional<Boolean> force) throws LuaException {
        slot = slot-1;
        IPeripheral peripheral = computer.getAvailablePeripheral(inv);
        if (inv == null) throw new LuaException("the input " + inv + " was not found");
        IItemHandler handler = extractHandler(peripheral.getTarget());

        if(Utils.isFromMinecraft(computer,inv)){
            if(slot < 0 || slot > handler.getSlots()) throw new LuaException("Slot out of range");
            var stack = handler.getStackInSlot(slot);
            if(stack.hasTag() && stack.getItem() instanceof MobDataCardItem){
                var previousMob = entity.saveWithFullMetadata().getCompound("SpawnData").getCompound("entity").getString("id");
                entity.getSpawner().setEntityId(EntityType.byString(stack.getTag().getString("mob")).get());
                if(force.isPresent() && force.get()){
                    stack.getTag().remove("mob");
                }else{
                    if(!previousMob.equals("minecraft:pig")){
                        stack.getTag().putString("mob",previousMob);
                    }
                }
                stack.resetHoverName();
                CompoundTag tag = new CompoundTag();
                var saved = entity.getSpawner().save(tag);
                entity.getSpawner().load(entity.getLevel(),entity.getBlockPos(),saved);
                entity.setChanged();
                entity.getSpawner().getSpawnerEntity();
                return true;
            }
        }else{
            throw new LuaException("This block requires a vanilla inventory");
        }
        return false;
    }


    @LuaFunction(mainThread = true)
    public final String getCurrentlySpawningMob(){
        CompoundTag tag = new CompoundTag();
        tag = entity.getSpawner().save(tag);
        return tag.getCompound("SpawnData").getCompound("entity").getString("id");
    }

    @LuaFunction(mainThread = true)
    public final boolean captureSpawner(IComputerAccess computer, Optional<String> inv, Optional<Integer> slot) throws LuaException {
        CompoundTag tag = new CompoundTag();
        var saved = entity.getSpawner().save(tag);
        if(lastTime + 50 >= System.currentTimeMillis())     // Adds a bit of delay, or else the spawners dupe (don't ask me why they dupe)
            return false;
        if(inv.isPresent()){
            IPeripheral peripheral = computer.getAvailablePeripheral(inv.get());
            if (inv.get() == null) throw new LuaException("the input " + inv.get() + " was not found");
            IItemHandler handler = extractHandler(peripheral.getTarget());
            if(Utils.isFromMinecraft(computer,inv.get())) {
                if (slot.isPresent()){
                    slot = Optional.of(slot.get()-1);
                    if (slot.get() < 0 || slot.get() > handler.getSlots()) throw new LuaException("Slot out of range");
                    var stack = handler.getStackInSlot(slot.get());
                    if(stack.getItem() instanceof MobDataCardItem){
                        var blockPos = entity.getBlockPos();
                        entity.getLevel().destroyBlock(blockPos,true);
                        stack.getOrCreateTag().putString("mob",saved.getCompound("SpawnData").getCompound("entity").getString("id"));
                        stack.setHoverName(Component.translatable("item.peripherals.mob_data_card").append(" ("+stack.getOrCreateTag().getString("mob")+")"));
                        entity.getLevel().addFreshEntity(new ItemEntity(entity.getLevel(),blockPos.getX(),blockPos.getY(),blockPos.getZ(),new ItemStack(Items.SPAWNER)));
                        lastTime = System.currentTimeMillis();
                        return true;
                    }
                }
            }else{
                throw new LuaException("This block requires a vanilla inventory");
            }
        }else if(saved.getCompound("SpawnData").getCompound("entity").getString("id").equals("minecraft:pig")){
                var blockPos = entity.getBlockPos();
                entity.getLevel().destroyBlock(blockPos,true);
                entity.getLevel().addFreshEntity(new ItemEntity(entity.getLevel(),blockPos.getX(),blockPos.getY(),blockPos.getZ(),new ItemStack(Items.SPAWNER)));
                lastTime = System.currentTimeMillis();
                return true;
        }
        return false;
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
        if(world.getBlockState(pos).getBlock().equals(Blocks.SPAWNER)){
            entity = (SpawnerBlockEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> this);
        }
        return LazyOptional.empty();
    }
}
