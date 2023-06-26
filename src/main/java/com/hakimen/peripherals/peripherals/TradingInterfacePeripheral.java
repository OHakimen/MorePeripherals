package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.TradingInterfaceEntity;
import com.hakimen.peripherals.registry.BlockRegister;
import com.hakimen.peripherals.utils.Utils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TradingInterfacePeripheral implements IPeripheral, IPeripheralProvider {

    private TradingInterfaceEntity tileEntity;



    @NotNull
    @Override
    public String getType() {
        return "trading_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction(mainThread = true)
    public final String getProfession() throws LuaException{

        if(tileEntity.villager == null) throw new LuaException("villager not in range");
        return tileEntity.villager.getVillagerData().getProfession().name();
    }


    @LuaFunction(mainThread = true)
    public final List<Map<String,Map<String,Map<String,?>>>> getTrades() throws LuaException{
        if(tileEntity.villager == null) throw new LuaException("villager not in range");
        List<Map<String,Map<String,Map<String,?>>>> offerList = new ArrayList<>();
        List<MerchantOffer> offers = tileEntity.villager.getOffers().stream().toList();
        offers.forEach(offer -> {
            var map = new HashMap<String,Map<String,Map<String,?>>>();
            var itemSet = new HashMap<String,Map<String,?>>();
            var itemSet2 = new HashMap<String,Map<String,?>>();
            var itemSet3 = new HashMap<String,Map<String,?>>();

            var itemSetDetails = getItemInfo(EnchantmentHelper.getEnchantments(
                    offer.getBaseCostA()
            ),offer.getBaseCostA().getCount());

            itemSet.put(ForgeRegistries.ITEMS.getKey(offer.getBaseCostA().getItem()).toString(),itemSetDetails);
            map.put("costA",itemSet);

            var itemSetDetails2 = getItemInfo(EnchantmentHelper.getEnchantments(
                    offer.getCostB()
            ),offer.getCostB().getCount());
            itemSet2.put(ForgeRegistries.ITEMS.getKey(offer.getCostB().getItem()).toString(),itemSetDetails2);
            map.put("costB",itemSet2);

            var itemSetDetails3 = getItemInfo(EnchantmentHelper.getEnchantments(
                    offer.getResult()
            ),offer.getResult().getCount());
            itemSet3.put(ForgeRegistries.ITEMS.getKey(offer.getResult().getItem()).toString(),itemSetDetails3);
            map.put("result",itemSet3);

            offerList.add(map);
        });
        return offerList;
    }

    @LuaFunction(mainThread = true)
    public final boolean trade(IComputerAccess computer,String from,String to,int trade) throws LuaException {
        if(!Utils.isFromMinecraft(computer,from)){
            throw new LuaException("this method needs a vanilla inventory as input");
        }
        if(tileEntity.villager == null) throw new LuaException("villager not in range");
        if(trade-1 < 0 || trade-1 > tileEntity.villager.getOffers().stream().toList().size()) throw new LuaException("trade index out of range");
        MerchantOffer offer = tileEntity.villager.getOffers().stream().toList().get(trade-1);

        IPeripheral inputLocation = computer.getAvailablePeripheral(from);
        if(inputLocation == null) throw new LuaException("the input "+from+" was not found");
        var input = extractHandler(inputLocation.getTarget());

        IPeripheral outlocation = computer.getAvailablePeripheral(to);
        if(outlocation == null) throw new LuaException("the input "+to+" was not found");
        var result = extractHandler(outlocation.getTarget());

        boolean validA = false;
        boolean validB = false;
        int[] slots = {-1,-1};
        for (int slot = 0; slot < input.getSlots() ; slot++) {
            var test = input.getStackInSlot(slot);
            if(!validA){
                if(test.getCount() >= offer.getBaseCostA().getCount() &&
                        test.equals(offer.getBaseCostA())){
                    validA = true;
                    slots[0] = slot;
                }
            }
            if(!validB){
                if(test.getCount() >= offer.getCostB().getCount() &&
                        test.equals(offer.getCostB())){
                    validB = true;
                    slots[1] = slot;
                }
            }
        }
        if((validA && validB)||(!validB && validA)) {

            var out = offer.assemble();
            var rest = out.copy();

            if (!rest.isEmpty()) {
                for (int slot = 0; slot < result.getSlots(); slot++) {
                    rest = result.insertItem(slot, out, false);
                    if (rest.isEmpty()) {
                        break;
                    }
                    out = rest;
                }
            }
            if (rest.isEmpty()) {
                input.getStackInSlot(slots[0]).shrink(offer.getBaseCostA().getCount());
                if (validB) {
                    input.getStackInSlot(slots[1]).shrink(offer.getCostB().getCount());
                }
                return true;
            } else
                throw new LuaException("destination inventory full");
        }
        return false;
    }
    @LuaFunction(mainThread = true)
    public final void restock() throws LuaException{
        if(tileEntity.villager == null) throw new LuaException("villager not in range");
        tileEntity.villager.restock();
    }
    long lastTime;
    @LuaFunction(mainThread = true)
    public final void cycleTrades() throws LuaException{
        if(lastTime + 50 >= System.currentTimeMillis()) // Slow the trade cycling down a bit, because it can lead to crashes in servers if done too fast
            return;
        if(tileEntity.villager == null) throw new LuaException("villager not in range");
        var lastProfession = tileEntity.villager.getVillagerData().getProfession();

        tileEntity.villager.setVillagerData(tileEntity.villager.getVillagerData().setProfession(VillagerProfession.NONE));
        tileEntity.villager.setVillagerData(tileEntity.villager.getVillagerData().setLevel(1));
        tileEntity.villager.setVillagerXp(0);
        tileEntity.villager.setVillagerData(tileEntity.villager.getVillagerData().setProfession(lastProfession));
        lastTime = System.currentTimeMillis();
    }

    private static HashMap<String,Object> getItemInfo(Map<Enchantment,Integer> enchants,int count){
        var itemSetDetails = new HashMap<String,Object>();
        var enchantList = new HashMap<String,Integer>();
        enchants.forEach((e,i)->{
            enchantList.put(e.getDescriptionId(),i);
        });
        itemSetDetails.put("enchants",enchantList);
        itemSetDetails.put("count",count);
        return itemSetDetails;
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

    private static int moveItem( IItemHandler from, int fromSlot, IItemHandler to, int toSlot, final int limit )
    {
        // See how much we can get out of this slot
        ItemStack extracted = from.extractItem( fromSlot, limit, true );
        if( extracted.isEmpty() ) return 0;

        // Limit the amount to extract
        int extractCount = Math.min( extracted.getCount(), limit );
        extracted.setCount( extractCount );

        ItemStack remainder = toSlot < 0 ? ItemHandlerHelper.insertItem( to, extracted, false ) : to.insertItem( toSlot, extracted, false );
        int inserted = remainder.isEmpty() ? extractCount : extractCount - remainder.getCount();
        if( inserted <= 0 ) return 0;

        // Remove the item from the original inventory. Technically this could fail, but there's little we can do
        // about that.
        from.extractItem( fromSlot, inserted, false );
        return inserted;
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        if(world.getBlockState(pos).getBlock().equals(BlockRegister.tradingInterface.get())){
            this.tileEntity = (TradingInterfaceEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> this);
        }
        return LazyOptional.empty();
    }
}
