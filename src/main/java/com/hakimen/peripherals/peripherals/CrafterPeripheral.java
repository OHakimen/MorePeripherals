package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.client.containers.CrafterContainer;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

public class CrafterPeripheral implements IPeripheral, IPeripheralProvider {

    private Level level;

    @NotNull
    @Override
    public String getType() {
        return "crafter";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult craft(IComputerAccess computer, String fromName, String toName, Map<?, ?> craftingParameters){
        IPeripheral from = computer.getAvailablePeripheral(fromName);
        if (from == null)
            return MethodResult.of(false,"the input " + fromName + " was not found");
        IItemHandler fromHandler = extractHandler(from.getTarget());

        IPeripheral to = computer.getAvailablePeripheral(toName);
        if (to == null)
            return MethodResult.of(false,"the input " + toName + " was not found");
        IItemHandler toHandler = extractHandler(to.getTarget());


        CrafterContainer container = new CrafterContainer();
        ArrayList<ItemStack> remainders = new ArrayList<>();
        for (int i = 0; i < craftingParameters.keySet().size(); i++) {
            Object value = craftingParameters.keySet().toArray()[i];
            if (value instanceof Number number && craftingParameters.get(value) instanceof Number slot) {
                if (slot.intValue() != 0) {
                    ItemStack stack = fromHandler.getStackInSlot(slot.intValue() - 1);
                    if(stack.getItem().hasCraftingRemainingItem()){
                        remainders.add(stack.getItem().getCraftingRemainingItem().getDefaultInstance());
                    }
                    container.setItem(number.intValue() - 1, stack);
                }

            }
        }
        var placed = false;
        boolean canPlaceAllRemainders = true;

        var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
        if (recipe.isPresent()) {
            for (int i = 0; i < toHandler.getSlots(); i++) {
                var assemble = recipe.get().getResultItem(RegistryAccess.EMPTY).copy();
                var stack = toHandler.insertItem(i,assemble , true);
                if (stack.isEmpty()) {
                    placed = true;
                    toHandler.insertItem(i, assemble, false);

                    //Remainders
                    for (int j = 0; j < remainders.size(); j++) {
                        for (int k = 0; k < fromHandler.getSlots(); k++) {
                            var stack2 = fromHandler.insertItem(k, remainders.get(k), true);
                            if (!stack2.isEmpty()) {
                                canPlaceAllRemainders = false;
                                break;
                            }
                        }
                        if(canPlaceAllRemainders) {
                            for (int k = 0; k < fromHandler.getSlots(); k++) {
                                var stack2 = fromHandler.insertItem(k, remainders.get(k), false);
                                if (stack2.isEmpty()) {
                                    break;
                                }
                            }
                        }else{
                            break;
                        }
                    }
                    if(placed){
                        for (int j = 0; j < craftingParameters.keySet().size(); j++) {
                            Object value = craftingParameters.keySet().toArray()[j];
                            if (craftingParameters.get(value) instanceof Number slot) {
                                if (slot.intValue() != 0) {
                                    fromHandler.extractItem(slot.intValue() - 1, 1, false);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            if(!placed && !canPlaceAllRemainders){
                return MethodResult.of(false,"failed to Craft");
            }
        }
        return MethodResult.of(true);
    }


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
        this.level = world;
        if(world.getBlockState(pos).getBlock().equals(Blocks.CRAFTING_TABLE)){
            return LazyOptional.of(() -> this);
        }
        return LazyOptional.empty();
    }
}
