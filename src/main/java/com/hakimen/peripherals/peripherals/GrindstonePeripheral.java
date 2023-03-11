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
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GrindstonePeripheral implements IPeripheral, IPeripheralProvider {

    @NotNull
    @Override
    public String getType() {
        return "grindstone_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction
    public boolean combine(IComputerAccess computer, String from, int fromSlot, String resource, int resourceSlot) throws LuaException {

        if(!Utils.isFromMinecraft(computer,from)){
            throw new LuaException("This method requires a vanilla inventory");
        }
        if(!Utils.isFromMinecraft(computer,resource)){
            throw new LuaException("This method requires a vanilla inventory");
        }

        fromSlot -= 1;
        resourceSlot -= 1; //Java starts counting at zero, but lua starts at 1
        if(from.matches(resource) && fromSlot == resourceSlot) throw new LuaException("Can't combine item with itself");
        IPeripheral input = computer.getAvailablePeripheral(from);
        if (input == null) throw new LuaException("the input " + from + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());
        if(fromSlot < 0 || fromSlot > inputHandler.getSlots()) throw new LuaException("from slot out of range");
        ItemStack itemstack = inputHandler.getStackInSlot(fromSlot);

        IPeripheral resourcesInput = computer.getAvailablePeripheral(resource);
        if (resourcesInput == null) throw new LuaException("the resources input " + resource + " was not found");
        IItemHandler resourcesInputHandler = extractHandler(resourcesInput.getTarget());
        if(resourceSlot < 0 || resourceSlot > resourcesInputHandler.getSlots()) throw new LuaException("resource slot out of range");
        ItemStack itemstack1 = resourcesInputHandler.getStackInSlot(resourceSlot);

        int j = 1;
        int i;
        ItemStack itemstack2;

        if (!itemstack.is(itemstack1.getItem())) {
            inputHandler.insertItem(fromSlot,itemstack,false);
            resourcesInputHandler.insertItem(resourceSlot,itemstack1,false);
            throw new LuaException("Items aren't the same type");
        }

        int k = itemstack.getMaxDamage() - itemstack.getDamageValue();
        int l = itemstack.getMaxDamage() - itemstack1.getDamageValue();
        int i1 = k + l + itemstack.getMaxDamage() * 5 / 100;
        i = Math.max(itemstack.getMaxDamage() - i1, 0);
        itemstack2 = this.mergeEnchants(itemstack, itemstack1);
        if (!itemstack2.isRepairable()) i = itemstack.getDamageValue();
        if (!itemstack2.isDamageableItem() || !itemstack2.isRepairable()) {
           if (!ItemStack.matches(itemstack, itemstack1)) {
               inputHandler.insertItem(fromSlot,itemstack,false);
               resourcesInputHandler.insertItem(resourceSlot,itemstack1,false);
               throw new LuaException("items don't match");
           }

           j = 2;
        }
        inputHandler.extractItem(fromSlot,1,false);
        resourcesInputHandler.extractItem(resourceSlot,1,false);
        itemstack2.setDamageValue(i);
        inputHandler.insertItem(fromSlot,itemstack2,false);
        return true;
    }

    @LuaFunction
    public void disenchant(IComputerAccess computer, String from, int slot, Optional<String> collector) throws LuaException {

        if(!Utils.isFromMinecraft(computer,from)){
            throw new LuaException("This method requires a vanilla inventory");
        }

        slot = slot+1;
        IPeripheral input = computer.getAvailablePeripheral(from);
        if (input == null) throw new LuaException("the input " + from + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());
        if(slot < 1 || slot > inputHandler.getSlots()) throw new LuaException("slot out of range");

        IPeripheral collectorInput;
        XPCollectorPeripheral collectorPeripheral;

        if(inputHandler.getStackInSlot(slot).is(Items.ENCHANTED_BOOK)){
            inputHandler.extractItem(slot,1,false);
            inputHandler.insertItem(slot,Items.BOOK.getDefaultInstance(),false);
        }else{
            inputHandler.getStackInSlot(slot).removeTagKey("Enchantments");
        }

        if(collector.isPresent()) {
            collectorInput = computer.getAvailablePeripheral(collector.get());
            collectorPeripheral = (XPCollectorPeripheral) collectorInput;

            collectorPeripheral.tileEntity.xpPoints += 8;
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

    private ItemStack mergeEnchants(ItemStack p_39591_, ItemStack p_39592_) {
      ItemStack itemstack = p_39591_.copy();
      Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_39592_);

      for(Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
         Enchantment enchantment = entry.getKey();
         if (!enchantment.isCurse() || EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemstack) == 0) {
            itemstack.enchant(enchantment, entry.getValue());
         }
      }

      return itemstack;
    }

    private ItemStack removeNonCurses(ItemStack p_39580_, int p_39581_, int p_39582_) {
        ItemStack itemstack = p_39580_.copy();
        itemstack.removeTagKey("Enchantments");
        itemstack.removeTagKey("StoredEnchantments");
        if (p_39581_ > 0) {
            itemstack.setDamageValue(p_39581_);
        } else {
            itemstack.removeTagKey("Damage");
        }

        itemstack.setCount(p_39582_);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_39580_).entrySet().stream().filter((p_39584_) -> {
            return p_39584_.getKey().isCurse();
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.setEnchantments(map, itemstack);
        itemstack.setRepairCost(0);
        if (itemstack.is(Items.ENCHANTED_BOOK) && map.size() == 0) {
            itemstack = new ItemStack(Items.BOOK);
            if (p_39580_.hasCustomHoverName()) {
                itemstack.setHoverName(p_39580_.getHoverName());
            }
        }

        for(int i = 0; i < map.size(); ++i) {
            itemstack.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(itemstack.getBaseRepairCost()));
        }

        return itemstack;
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        if(world.getBlockState(pos).getBlock().equals(Blocks.GRINDSTONE)){

            return LazyOptional.of(() -> this);
        }
        return LazyOptional.empty();
    }
}
