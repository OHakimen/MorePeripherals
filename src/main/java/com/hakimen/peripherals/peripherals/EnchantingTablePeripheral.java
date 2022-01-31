package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.Peripherals;
import com.hakimen.peripherals.blocks.tile_entities.EnchantingTableInterfaceEntity;
import com.hakimen.peripherals.utils.EnchantUtils;
import com.hakimen.peripherals.utils.Utils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnchantingTablePeripheral implements IPeripheral {


    private final EnchantingTableInterfaceEntity tileEntity;


    public EnchantingTablePeripheral(EnchantingTableInterfaceEntity tileEntity) {
        this.tileEntity = tileEntity;

    }

    @NotNull
    @Override
    public String getType() {
        return "enchanting_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction
    public List<String> getEnchantsFor(IComputerAccess computer, String from, int slot) throws LuaException {
        if (tileEntity.enchantTable == null) {
            throw new LuaException("there is no enchanting table near the interface");
        }
        List<String> enchants = new ArrayList<>();
        IPeripheral input = computer.getAvailablePeripheral(from);
        if (input == null) throw new LuaException("the input " + from + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());
        if(slot < 0 || slot > inputHandler.getSlots()) throw new LuaException("slot out of range");
        if (tileEntity.enchantTable == null) {
            throw new LuaException("there is no enchanting table near the interface");
        }
        var item = inputHandler.getStackInSlot(slot);
        if (item.isEnchantable()) {
            if (!item.isEnchanted()) {
                for (EnchantmentCategory category : EnchantmentCategory.values()) {
                    for (Enchantment e : Registry.ENCHANTMENT) {
                        if (e.category == category && e.canApplyAtEnchantingTable(item) && !e.isTreasureOnly()) {
                            enchants.add(e.getRegistryName().toString());
                        }
                    }
                }
            } else {
                throw new LuaException("this item is already enchanted");
            }
        } else {
            throw new LuaException("this item can not be enchanted");
        }
        return enchants;
    }

    @LuaFunction
    public boolean enchant(IComputerAccess computer, String from, int slot, String resources) throws LuaException {
        if(!Utils.isFromMinecraft(computer,resources)){
            throw new LuaException("this method needs a vanilla inventory as the resources input");
        }
        if (tileEntity.enchantTable == null) {
            throw new LuaException("there is no enchanting table near the interface");
        }
        var bottlesNeeded = 8;

        IPeripheral input = computer.getAvailablePeripheral(from);
        if (input == null) throw new LuaException("the input " + from + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());
        if(slot < 0 || slot > inputHandler.getSlots()) throw new LuaException("slot out of range");

        IPeripheral resourcesInput = computer.getAvailablePeripheral(resources);
        if (resourcesInput == null) throw new LuaException("the resources input " + resources + " was not found");
        IItemHandler resourcesInputHandler = extractHandler(resourcesInput.getTarget());

        boolean foundLapis = false,foundEXP = false;
        int lapisSlot=-1,expSlot=-1;
        for (int i = 0; i < resourcesInputHandler.getSlots(); i++) {
            if(!foundLapis){
                if(resourcesInputHandler.getStackInSlot(i).is(Items.LAPIS_LAZULI) && resourcesInputHandler.getStackInSlot(i).getCount()>=1){
                    lapisSlot=i;
                    foundLapis = true;
                }
            }
            if(!foundEXP){
                if(resourcesInputHandler.getStackInSlot(i).is(Items.EXPERIENCE_BOTTLE) && resourcesInputHandler.getStackInSlot(i).getCount() >= bottlesNeeded){
                    expSlot = i;
                    foundEXP = true;
                }
            }
        }
        if(!foundEXP){
            throw new LuaException("not found the required "+bottlesNeeded+" xp bottles");
        }
        if(!foundLapis){
            throw new LuaException("not found the required lapis lazuli");
        }
        if(foundEXP && foundLapis){
            resourcesInputHandler.extractItem(lapisSlot,1,false);
            resourcesInputHandler.extractItem(expSlot,bottlesNeeded,false);
            var item = inputHandler.getStackInSlot(slot);
            boolean isBook = item.getItem() == Items.BOOK;
            var bookItem = item.copy();
            if(isBook){
                inputHandler.extractItem(slot,1,false);
                var enchant = Registry.ENCHANTMENT.getRandom(tileEntity.getLevel().random);
                var value = tileEntity.getLevel().random.nextInt(enchant.getMinLevel(), enchant.getMaxLevel()+1);
                bookItem = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchant,value));


                inputHandler.insertItem(slot,bookItem,false);
                return true;
            }
            else if (!item.isEnchanted()) {
                if (item.isEnchantable()) {
                    EnchantUtils.addRandomEnchant(tileEntity.getLevel().random,item);
                    return true;

                } else {
                    throw new LuaException("this item is not enchatable");
                }
            } else {
                throw new LuaException("this item is already enchanted");
            }

        }
        return false;
    }

    @javax.annotation.Nullable
    private static IItemHandler extractHandler(@javax.annotation.Nullable Object object) {
        if (object instanceof BlockEntity blockEntity && blockEntity.isRemoved()) return null;

        if (object instanceof ICapabilityProvider provider) {
            LazyOptional<IItemHandler> cap = provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (cap.isPresent()) return cap.orElseThrow(NullPointerException::new);
        }

        if (object instanceof IItemHandler handler) return handler;
        if (object instanceof Container container) return new InvWrapper(container);
        return null;
    }

    private static int moveItem(IItemHandler from, int fromSlot, IItemHandler to, int toSlot, final int limit) {
        // See how much we can get out of this slot
        ItemStack extracted = from.extractItem(fromSlot, limit, true);
        if (extracted.isEmpty()) return 0;

        // Limit the amount to extract
        int extractCount = Math.min(extracted.getCount(), limit);
        extracted.setCount(extractCount);

        ItemStack remainder = toSlot < 0 ? ItemHandlerHelper.insertItem(to, extracted, false) : to.insertItem(toSlot, extracted, false);
        int inserted = remainder.isEmpty() ? extractCount : extractCount - remainder.getCount();
        if (inserted <= 0) return 0;

        // Remove the item from the original inventory. Technically this could fail, but there's little we can do
        // about that.
        from.extractItem(fromSlot, inserted, false);
        return inserted;
    }
}
