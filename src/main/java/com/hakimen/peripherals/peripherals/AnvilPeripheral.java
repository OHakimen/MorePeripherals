package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.AnvilInterfaceEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AnvilPeripheral implements IPeripheral {


    private final AnvilInterfaceEntity tileEntity;


    public AnvilPeripheral(AnvilInterfaceEntity tileEntity) {
        this.tileEntity = tileEntity;

    }

    @NotNull
    @Override
    public String getType() {
        return "anvil_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction
    public boolean combine(IComputerAccess computer, String from, int fromSlot, String resource, int resourceSlot, String xpSource) throws LuaException {
        if (tileEntity.anvil == null) {
            throw new LuaException("there is no anvil near the interface");
        }
        fromSlot -= 1;
        resourceSlot -= 1; //Java starts counting at zero, but lua starts at 1
        int i = 0;
        int j = 0;
        int k = 0;

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
        ItemStack itemstack2 = resourcesInputHandler.getStackInSlot(resourceSlot);

        IPeripheral xpInput = computer.getAvailablePeripheral(xpSource);
        if (xpInput == null) throw new LuaException("the xp input " + xpSource + " was not found");
        IItemHandler xpInputHandler = extractHandler(xpInput.getTarget());
        var bottlesNeeded = 8;
        int xpSlot=-1;
        for (int m = 0; m < xpInputHandler.getSlots(); m++) {
                if(xpInputHandler.getStackInSlot(m).is(Items.EXPERIENCE_BOTTLE) && xpInputHandler.getStackInSlot(m).getCount() >= bottlesNeeded){
                    xpSlot = m;
                    break;
                }
        }
        if(xpSlot < 0) throw new LuaException("Not enough bottles found");

        ItemStack itemstack1 = itemstack.copy();
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
        j += itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
        int repairItemCountCost = 0;
        boolean flag = false;

        flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
        if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
            int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
            if (l2 <= 0) throw new LuaException("Item doesn't need repairing");

            int i3;
            for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                int j3 = itemstack1.getDamageValue() - l2;
                itemstack1.setDamageValue(j3);
                ++i;
                l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
            }

            repairItemCountCost = i3;
        } else {
            if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                throw new LuaException("Item can't be combined this way");
            }

            if (itemstack1.isDamageableItem() && !flag) {
                int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                int k1 = l + j1;
                int l1 = itemstack1.getMaxDamage() - k1;
                if (l1 < 0) {
                    l1 = 0;
                }

                if (l1 < itemstack1.getDamageValue()) {
                    itemstack1.setDamageValue(l1);
                    i += 2;
                }
            }

            Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
            boolean flag2 = false;
            boolean flag3 = false;

            for(Enchantment enchantment1 : map1.keySet()) {
                if (enchantment1 != null) {
                    int i2 = map.getOrDefault(enchantment1, 0);
                    int j2 = map1.get(enchantment1);
                    j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                    boolean flag1 = enchantment1.canEnchant(itemstack);
                    if (itemstack.is(Items.ENCHANTED_BOOK)) {
                        flag1 = true;
                    }

                    for(Enchantment enchantment : map.keySet()) {
                        if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                            flag1 = false;
                            ++i;
                        }
                    }

                    if (!flag1) {
                        flag3 = true;
                    } else {
                        flag2 = true;
                        if (j2 > enchantment1.getMaxLevel()) {
                            j2 = enchantment1.getMaxLevel();
                        }

                        map.put(enchantment1, j2);
                        int k3 = 0;
                        switch(enchantment1.getRarity()) {
                            case COMMON:
                                k3 = 1;
                                break;
                            case UNCOMMON:
                                k3 = 2;
                                break;
                            case RARE:
                                k3 = 4;
                                break;
                            case VERY_RARE:
                                k3 = 8;
                        }

                        if (flag) {
                            k3 = Math.max(1, k3 / 2);
                        }

                        i += k3 * j2;
                        if (itemstack.getCount() > 1) {
                            i = 40;
                        }
                    }
                }
            }

            if (flag3 && !flag2) {
                throw new LuaException("Enchantments are not compatible");
            }
        }
        if (flag && !itemstack1.isBookEnchantable(itemstack2)) throw new LuaException("Item is not enchantable with this book");
        int k2 = itemstack1.getBaseRepairCost();
        if (!itemstack2.isEmpty() && k2 < itemstack2.getBaseRepairCost()) {
            k2 = itemstack2.getBaseRepairCost();
        }

        if (k != i || k == 0) {
            k2 = AnvilMenu.calculateIncreasedRepairCost(k2);
        }

        itemstack1.setRepairCost(k2);
        EnchantmentHelper.setEnchantments(map, itemstack1);

        inputHandler.extractItem(fromSlot,1,false);
        resourcesInputHandler.extractItem(resourceSlot,1,false);
        xpInputHandler.extractItem(xpSlot,8,false);
        inputHandler.insertItem(fromSlot,itemstack1,false);

        return true;
    }

    @LuaFunction
    public void rename(IComputerAccess computer, String from, int slot, String name) throws LuaException {

        slot -= 1;

        IPeripheral input = computer.getAvailablePeripheral(from);
        if (input == null) throw new LuaException("the input " + from + " was not found");
        IItemHandler inputHandler = extractHandler(input.getTarget());
        if(slot < 0 || slot > inputHandler.getSlots()) throw new LuaException("from slot out of range");
        ItemStack itemstack = inputHandler.getStackInSlot(slot);

        if(name.matches("")) {
            itemstack.resetHoverName();
            return;
        } else {
            itemstack.setHoverName(new TextComponent(name));
        }

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

}
