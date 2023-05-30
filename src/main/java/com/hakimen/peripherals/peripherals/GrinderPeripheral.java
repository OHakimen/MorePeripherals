package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.GrinderEntity;
import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GrinderPeripheral implements IPeripheral {


    private final GrinderEntity tileEntity;
    private final FakePlayer fakePlayer;

    public GrinderPeripheral(GrinderEntity tileEntity) {
        this.tileEntity = tileEntity;
        fakePlayer = new FakePlayer((ServerLevel) tileEntity.getLevel(),new GameProfile(UUID.randomUUID(),"Grinder"));

    }

    @NotNull
    @Override
    public String getType() {
        return "grinder";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction(mainThread = true)
    public final void attack(){
        List<Entity> entities = tileEntity.getLevel().getEntities(null,new AABB(tileEntity.getBlockPos().below().north().east(2),
                tileEntity.getBlockPos().above(2).south(2).west(2)));
        for (Entity entity:entities){
            if(entity instanceof LivingEntity livingEntity){
                fakePlayer.setItemInHand(InteractionHand.MAIN_HAND,tileEntity.inventory.getStackInSlot(0));
                fakePlayer.attack(entity);
                livingEntity.invulnerableTime = 0;
                if(tileEntity.inventory.getStackInSlot(0).getItem() instanceof SwordItem sword){
                    livingEntity.hurt(DamageSource.playerAttack(fakePlayer),
                            (sword).getDamage());
                }
            }
        }
    }

    @LuaFunction(mainThread = true)
    public final boolean hasSword(){
        return tileEntity.inventory.getStackInSlot(0).getItem() instanceof SwordItem;
    }
    @LuaFunction(mainThread = true)
    public final boolean pushSword(IComputerAccess computer,String from, int slot) throws LuaException {
        IPeripheral input = computer.getAvailablePeripheral(from);
        if (input == null) throw new LuaException("the input " + from + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());
        if(slot < 0 || slot > inputHandler.getSlots()) throw new LuaException("slot out of range");
        var stack = inputHandler.getStackInSlot(slot);
        if(stack.getItem() instanceof SwordItem){
            if(!(tileEntity.inventory.getStackInSlot(0).getItem() instanceof SwordItem)){
                tileEntity.inventory.setStackInSlot(0,stack.copy());
                inputHandler.extractItem(slot,1,false);
                return true;
            }else{
                throw new LuaException("there is a sword in the grinder already");
            }
        }else{
            return false;
        }
    }
    @LuaFunction(mainThread = true)
    public final void pullSword(IComputerAccess computer,String to) throws LuaException {
        IPeripheral input = computer.getAvailablePeripheral(to);
        if (input == null) throw new LuaException("the output " + to + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());

        var stack = tileEntity.inventory.getStackInSlot(0).copy();
        var sent = false;
        for (int i = 0; i < inputHandler.getSlots(); i++) {
            if(inputHandler.getStackInSlot(i).isEmpty()){
                inputHandler.insertItem(i,stack,false);
                tileEntity.inventory.extractItem(0,1,false);
                sent = true;
                break;
            }
        }
        if(!sent){
           throw new LuaException("target inventory is full");
        }
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
}
