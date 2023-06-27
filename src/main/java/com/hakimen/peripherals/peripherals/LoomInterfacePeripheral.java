package com.hakimen.peripherals.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.Registry.BANNER_PATTERN;

public class LoomInterfacePeripheral implements IPeripheral, IPeripheralProvider {

    @NotNull
    @Override
    public String getType() {
        return "loom_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction(mainThread = true)
    public MethodResult paintBanner(IComputerAccess computer, String from, int slotBanner, int slotDye, int pattern)  {


        IPeripheral inputPeripheral = computer.getAvailablePeripheral(from);
        if (inputPeripheral == null) return MethodResult.of(false,"the input " + from + " was not found");
        var input = extractHandler(inputPeripheral.getTarget());

        if(slotBanner < 0 || slotBanner > input.getSlots()) return MethodResult.of(false,"banner slot out of range");
        if(slotDye < 0 || slotDye > input.getSlots()) return MethodResult.of(false,"dye slot out of range");

        var banner = input.getStackInSlot(slotBanner);
        var dye = input.getStackInSlot(slotDye);

        if (!(banner.getItem() instanceof BannerItem)) {
            return MethodResult.of(false,"the item in " + slotBanner + " is not a banner");
        }
        if (!(dye.getItem() instanceof DyeItem)) {
            return MethodResult.of(false,"the item in " + slotDye + " is not a dye");
        }
        if (dye.getCount() < banner.getCount()) {
           return MethodResult.of(false,"not enough dye");
        }
        if(pattern > BANNER_PATTERN.stream().toList().size() || pattern < 0) {
            return MethodResult.of(false,"invalid pattern");
        }
        var patternTag = new CompoundTag();
        patternTag.putString("Pattern", BANNER_PATTERN.stream().toList().get(pattern).getHashname());
        patternTag.putInt("Color", DyeColor.getColor(dye).getId());
        input.extractItem(slotDye,banner.getCount(),false);
        var ListPatterns = new ListTag();
        var blockEntityData = BlockItem.getBlockEntityData(banner);

        if (blockEntityData != null && blockEntityData.contains("Patterns", 9)) {
            ListPatterns = blockEntityData.getList("Patterns", 10);
        } else {
            ListPatterns = new ListTag();
            if (blockEntityData == null) {
                blockEntityData = new CompoundTag();
            }

            blockEntityData.put("Patterns", ListPatterns);
        }
        ListPatterns.add(patternTag);
        BlockItem.setBlockEntityData(banner, BlockEntityType.BANNER, blockEntityData);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult clearBanner(IComputerAccess computer,String from,int slot)  {


        IPeripheral inputPeripheral = computer.getAvailablePeripheral(from);
        if (inputPeripheral == null) return MethodResult.of(false,"the input " + from + " was not found");
        var input = extractHandler(inputPeripheral.getTarget());
        if(slot < 0 || slot > input.getSlots()) return MethodResult.of(false,"slot out of range");
        var banner = input.getStackInSlot(slot);
        if(!(banner.getItem() instanceof BannerItem)){
            return MethodResult.of(false,"not a banner");
        }
        BlockItem.setBlockEntityData(banner,BlockEntityType.BANNER,new CompoundTag());
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

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        if(world.getBlockState(pos).getBlock().equals(Blocks.LOOM)){
            return LazyOptional.of(() -> this);
        }
        return LazyOptional.empty();
    }
}
