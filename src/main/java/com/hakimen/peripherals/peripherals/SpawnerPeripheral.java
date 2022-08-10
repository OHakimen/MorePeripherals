package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.SpawnerInterfaceEntity;
import com.hakimen.peripherals.items.MobDataCardItem;
import com.hakimen.peripherals.utils.Utils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SpawnerPeripheral implements IPeripheral {

    SpawnerInterfaceEntity entity;
    public SpawnerPeripheral(SpawnerInterfaceEntity entity){
        this.entity = entity;
    }
    @NotNull
    @Override
    public String getType() {
        return "spawner_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction
    public final boolean changeSpawner(IComputerAccess computer, String inv, int slot) throws LuaException {
        slot = slot-1;
        IPeripheral peripheral = computer.getAvailablePeripheral(inv);
        if (inv == null) throw new LuaException("the input " + inv + " was not found");
        IItemHandler handler = extractHandler(peripheral.getTarget());

        if(Utils.isFromMinecraft(computer,inv)){
            if(slot < 0 || slot > handler.getSlots()) throw new LuaException("Slot out of range");
            var stack = handler.getStackInSlot(slot);
            if(stack.hasTag() && stack.getItem() instanceof MobDataCardItem){
                System.out.println(EntityType.byString(stack.getTag().getString("mob")));
                entity.entity.getSpawner().setEntityId(EntityType.byString(stack.getTag().getString("mob")).get());
                stack.getTag().remove("mob");
                stack.resetHoverName();
                CompoundTag tag = new CompoundTag();
                var saved = entity.entity.getSpawner().save(tag);
                entity.entity.getSpawner().load(entity.entity.getLevel(),entity.entity.getBlockPos(),saved);
                entity.entity.setChanged();
                entity.entity.getSpawner().getSpawnerEntity();
                return true;

            }
        }else{
            throw new LuaException("This block requires a vanilla inventory");
        }
        return false;
    }


    @LuaFunction
    public final String getCurrentlySpawningMob(){
        CompoundTag tag = new CompoundTag();
        tag = entity.entity.getSpawner().save(tag);
        return tag.getCompound("SpawnData").getCompound("entity").getString("id");
    }

    @LuaFunction
    public final boolean captureSpawner(IComputerAccess computer, Optional<String> inv, Optional<Integer> slot) throws LuaException {
        ItemStack spawnerBlock = new ItemStack(Items.SPAWNER);

        CompoundTag tag = new CompoundTag();
        var saved = entity.entity.getSpawner().save(tag);

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
                        stack.getOrCreateTag().putString("mob",saved.getCompound("SpawnData").getCompound("entity").getString("id"));
                        stack.setHoverName(new TranslatableComponent("item.peripherals.mob_data_card").append(" ("+stack.getOrCreateTag().getString("mob")+")"));
                        var blockPos = entity.entity.getBlockPos();
                        entity.entity.getLevel().addFreshEntity(new ItemEntity(entity.entity.getLevel(),blockPos.getX(),blockPos.getY(),blockPos.getZ(),spawnerBlock));
                        entity.entity.getLevel().destroyBlock(blockPos,false);
                        return true;
                    }
                }
            }else{
                throw new LuaException("This block requires a vanilla inventory");
            }
        }else if(saved.getCompound("SpawnData").getCompound("entity").getString("id").equals("minecraft:pig")){
                var blockPos = entity.entity.getBlockPos();
                entity.entity.getLevel().addFreshEntity(new ItemEntity(entity.entity.getLevel(),blockPos.getX(),blockPos.getY(),blockPos.getZ(),spawnerBlock));
                entity.entity.getLevel().destroyBlock(blockPos,false);
                return true;
        }
        return false;
    }

    @Nullable
    private static IItemHandler extractHandler(@Nullable Object object) {
        if (object instanceof BlockEntity blockEntity && blockEntity.isRemoved()) return null;

        if (object instanceof ICapabilityProvider provider) {
            LazyOptional<IItemHandler> cap = provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (cap.isPresent()) return cap.orElseThrow(NullPointerException::new);
        }

        if (object instanceof IItemHandler handler) return handler;
        if (object instanceof Container container) return new InvWrapper(container);
        return null;
    }

}
