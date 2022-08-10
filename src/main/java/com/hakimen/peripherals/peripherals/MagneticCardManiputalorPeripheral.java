package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.AdvancedDiskRaidEntity;
import com.hakimen.peripherals.blocks.tile_entities.MagneticCardManiputalorEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.MediaProviders;
import dan200.computercraft.shared.media.items.ItemDisk;
import dan200.computercraft.shared.util.StringUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MagneticCardManiputalorPeripheral implements IPeripheral {


    private final MagneticCardManiputalorEntity tileEntity;


    public MagneticCardManiputalorPeripheral(MagneticCardManiputalorEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return "magnetic_card_manipulator";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction
    public final String readCard(){
        return tileEntity.inventory.getStackInSlot(0).getOrCreateTag().getString("data");
    }
    @LuaFunction
    public final void writeCard(String data){
        tileEntity.inventory.getStackInSlot(0).getOrCreateTag().putString("data",data);
    }
    @LuaFunction
    public final void ejectCard(){
        tileEntity.getLevel().addFreshEntity(new ItemEntity(
                tileEntity.getLevel(),
                tileEntity.getBlockPos().getX() + 0.5,
                tileEntity.getBlockPos().getY() + 0.5,
                tileEntity.getBlockPos().getZ() + 0.5,
                tileEntity.inventory.extractItem(0,1,false)
        ));
    }

    @LuaFunction
    public final void setLabel(String label){
        if(label.equals("")){
            tileEntity.inventory.getStackInSlot(0).resetHoverName();
        }else{
            tileEntity.inventory.getStackInSlot(0).setHoverName(Component.literal(label));
        }
    }
    @LuaFunction
    public final String getLabel(){
        return tileEntity.inventory.getStackInSlot(0).getHoverName().getString();
    }

    @LuaFunction
    public final void setSensibility(boolean sensibility){
        tileEntity.inventory.getStackInSlot(0).getOrCreateTag().putBoolean("sensible",sensibility);
    }

}
