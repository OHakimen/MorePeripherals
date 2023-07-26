package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.GrinderEntity;
import com.hakimen.peripherals.registry.BlockRegister;
import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
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

public class GrinderPeripheral implements IPeripheral, IPeripheralProvider {
    private GrinderEntity tileEntity;
    private FakePlayer fakePlayer;


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
        List<Entity> entities = tileEntity.getLevel().getEntities(null,new AABB(tileEntity.getBlockPos()).inflate(2,2,2));
        for (Entity entity:entities){
            if(entity instanceof LivingEntity livingEntity){
                fakePlayer.setItemInHand(InteractionHand.MAIN_HAND,tileEntity.inventory.getStackInSlot(0));
                fakePlayer.attack(entity);
                livingEntity.invulnerableTime = 0;
                if(tileEntity.inventory.getStackInSlot(0).getItem() instanceof SwordItem sword){
                    livingEntity.hurt(new DamageSources(RegistryAccess.EMPTY).playerAttack(fakePlayer),
                            (sword).getDamage());
                }
            }
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult hasSword(){
        return MethodResult.of(true,tileEntity.inventory.getStackInSlot(0).getItem() instanceof SwordItem);
    }
    @LuaFunction(mainThread = true)
    public final MethodResult pushSword(IComputerAccess computer,String from, int slot) {
        IPeripheral input = computer.getAvailablePeripheral(from);
        if (input == null)
            return MethodResult.of(false,"the input " + from + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());
        if(slot < 0 || slot > inputHandler.getSlots())
            return MethodResult.of(false,"slot out of range");
        var stack = inputHandler.getStackInSlot(slot);
        if(stack.getItem() instanceof SwordItem){
            if(!(tileEntity.inventory.getStackInSlot(0).getItem() instanceof SwordItem)){
                tileEntity.inventory.setStackInSlot(0,stack.copy());
                inputHandler.extractItem(slot,1,false);
                return MethodResult.of(true);
            }else{
                return MethodResult.of(false,"there is a sword in the grinder already");
            }
        }else{
            return MethodResult.of(false);
        }
    }
    @LuaFunction(mainThread = true)
    public final MethodResult pullSword(IComputerAccess computer,String to) {
        IPeripheral input = computer.getAvailablePeripheral(to);
        if (input == null)
            return MethodResult.of(false,"the output " + to + " was not found");
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
           return MethodResult.of(false,"target inventory is full");
        }
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

    @Override
    public LazyOptional<IPeripheral> getPeripheral(Level world, BlockPos pos, Direction side) {
        if(world.getBlockState(pos).getBlock().equals(BlockRegister.grinder.get())){
            var peripheral = new GrinderPeripheral();
            peripheral.tileEntity = (GrinderEntity) world.getBlockEntity(pos);
            peripheral.fakePlayer = new FakePlayer((ServerLevel) world,new GameProfile(UUID.randomUUID(),"Grinder"));
            return LazyOptional.of(() -> peripheral);
        }
        return LazyOptional.empty();
    }
}
