package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.PlayerInterfaceBlock;
import com.hakimen.peripherals.blocks.tile_entities.PlayerInterfaceEntity;
import com.hakimen.peripherals.items.PlayerCardItem;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static dan200.computercraft.core.util.ArgumentHelpers.assertBetween;

public class PlayerInterfacePeripheral implements IPeripheral, IPeripheralProvider {


    PlayerInterfaceEntity entity;

    public PlayerInterfacePeripheral() {
    }


    @Override
    public String getType() {
        return "player_interface";
    }


    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    public boolean hasCard() {
        return entity.inventory.getStackInSlot(0).getItem() instanceof PlayerCardItem;
    }

    public boolean cardIsBound() {
        return entity.inventory.getStackInSlot(0).getOrCreateTag().contains("Owner", Tag.TAG_STRING);
    }
    public Player getPlayer() {
        UUID ownerUUID = entity.inventory.getStackInSlot(0).getOrCreateTag().getUUID("Bind");
        Player player = entity.getLevel().getServer().getPlayerList().getPlayer(ownerUUID);
        return player;
    }

    public Inventory getInventory() {
        UUID ownerUUID = entity.inventory.getStackInSlot(0).getOrCreateTag().getUUID("Bind");
        Player player = entity.getLevel().getServer().getPlayerList().getPlayer(ownerUUID);
        return player != null ? player.getInventory() : null;
    }

    @LuaFunction(mainThread = true)
    public MethodResult getPlayerInfo(){
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        Player player = getPlayer();
        if(player == null){
            return MethodResult.of(false, "Player isn't present");
        }
        HashMap<String,Object> data = new HashMap<>();

        data.put("name", player.getDisplayName().getString());
        data.put("uuid", player.getUUID().toString());

        HashMap<String,Object> playerPos = new HashMap<>();

        playerPos.put("x", player.getX());
        playerPos.put("y", player.getY());
        playerPos.put("z", player.getZ());

        data.put("pos",playerPos);

        HashMap<String,Object> hungerData = new HashMap<>();
        hungerData.put("hunger",player.getFoodData().getFoodLevel());
        hungerData.put("saturation",player.getFoodData().getSaturationLevel());

        data.put("hunger", hungerData);

        data.put("yaw", player.getViewYRot(1));
        data.put("pitch", player.getViewXRot(1));

        HashMap<String,Object> effects = new HashMap<>();

        for (MobEffectInstance effect:player.getActiveEffects()) {
            HashMap<String,Object> currentEffect = new HashMap<>();
            currentEffect.put("duration", effect.getDuration()/20);
            currentEffect.put("amplifier", effect.getAmplifier());
            effects.put(ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).toString(),currentEffect);
        }

        data.put("effects",effects);

        data.put("dimension", player.level().dimension().location().toString());


        return MethodResult.of(data);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getEquipment(){
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        Player player = getPlayer();
        if(player == null){
            return MethodResult.of(false, "Player isn't present");
        }
        HashMap<String,Object> allEquipment = new HashMap<>();
        for (var equipmentSlot: EquipmentSlot.values()) {
            if(!player.getItemBySlot(equipmentSlot).isEmpty()) {
                allEquipment.put(equipmentSlot.getName(), VanillaDetailRegistries.ITEM_STACK.getDetails(player.getItemBySlot(equipmentSlot)));
            }
        }
        return MethodResult.of(allEquipment);
    }

    @LuaFunction(mainThread = true)
    public MethodResult list() {
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var inventory = getInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        Map<Integer, Map<String, ?>> result = new HashMap<>();
        var size = inventory.getContainerSize();
        for (var i = 0; i < size; i++) {
            var stack = inventory.getItem(i);
            if (!stack.isEmpty()) result.put(i + 1, VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack));
        }

        return MethodResult.of(result);
    }

    @LuaFunction(mainThread = true)
    public MethodResult size() {
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var inventory =  getInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        return MethodResult.of(inventory.getContainerSize());
    }

    @javax.annotation.Nullable
    @LuaFunction(mainThread = true)
    public MethodResult getItemDetail(int slot) throws LuaException {
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var inventory =  getInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        assertBetween(slot, 1, inventory.getContainerSize(), "Slot out of range (%s)");

        var stack = inventory.getItem(slot - 1);
        return MethodResult.of(stack.isEmpty() ? false : VanillaDetailRegistries.ITEM_STACK.getDetails(stack));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getItemLimit(int slot) throws LuaException {
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var inventory = getInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        assertBetween(slot, 1, inventory.getContainerSize(), "Slot out of range (%s)");
        return MethodResult.of(inventory.getItem(slot - 1).getMaxStackSize());
    }

    @LuaFunction(mainThread = true)
    public MethodResult pushItems(IComputerAccess computer,
                                  String toName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot
    ) throws LuaException {

        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var inventory =  getInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }

        // Find location to transfer to
        var location = computer.getAvailablePeripheral(toName);
        if (location == null) throw new LuaException("Target '" + toName + "' does not exist");

        var to = extractHandler(location.getTarget());
        if (to == null) throw new LuaException("Target '" + toName + "' is not an inventory");

        // Validate slots
        int actualLimit = limit.orElse(Integer.MAX_VALUE);
        assertBetween(fromSlot, 1, 36, "From slot out of range (%s)");
        if (toSlot.isPresent()) assertBetween(toSlot.get(), 1, to.getSlots(), "To slot out of range (%s)");

        if (actualLimit <= 0) return MethodResult.of(0);


        var extracted = inventory.removeItem(fromSlot - 1, limit.orElse(1));
        if (toSlot.isPresent()) {
            if (to.isItemValid(toSlot.get(), extracted)) {
                var inserted = to.insertItem(toSlot.get() - 1, extracted, false);
                if (inserted.getCount() == 0) {
                    return MethodResult.of(extracted.getCount());
                } else {
                    inventory.add(inserted);
                    return MethodResult.of(false, "Couldn't move items to slot " + (toSlot.get() - 1));
                }
            }
        }
        for (int i = 0; i < to.getSlots(); i++) {
            var inserted = to.insertItem(i, extracted, false);
            if (inserted.getCount() == 0) {
                break;
            }
            if (i == to.getSlots() - 1 && inserted.getCount() > 0) {
                inventory.add(inserted);
                return MethodResult.of(false, "Couldn't move items");
            }
        }
        return MethodResult.of(extracted.getCount());
    }

    @LuaFunction(mainThread = true)
    public MethodResult pullItems(IComputerAccess computer,
                                  String fromName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot
    ) throws LuaException {

        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var inventory =  getInventory();
        if (inventory == null) {
            return MethodResult.of(false, "Player isn't present");
        }

        // Find location to transfer to
        var location = computer.getAvailablePeripheral(fromName);
        if (location == null) throw new LuaException("Source '" + fromName + "' does not exist");

        var from = extractHandler(location.getTarget());
        if (from == null) throw new LuaException("Source '" + fromName + "' is not an inventory");

        // Validate slots
        int actualLimit = limit.orElse(Integer.MAX_VALUE);
        assertBetween(fromSlot, 1, from.getSlots(), "From slot out of range (%s)");
        if (toSlot.isPresent())
            assertBetween(toSlot.get(), 1, 36, "To slot out of range (%s)");

        if (actualLimit <= 0) return MethodResult.of(0);
        var extracted = from.extractItem(fromSlot - 1, limit.orElse(1), true);
        var count = extracted.getCount();
        if (canAddItem(inventory,extracted)) {
            extracted = from.extractItem(fromSlot - 1, limit.orElse(1), false);
            if (toSlot.isPresent()) {
                var itemInSlot = inventory.getItem(toSlot.get()-1);
                boolean canPlace = false;
                ItemStack placeStack = ItemStack.EMPTY;
                if (ItemStack.isSameItem(itemInSlot, ItemStack.EMPTY)) {
                    canPlace = true;
                    placeStack = extracted;
                } else if (ItemStack.isSameItem(itemInSlot, extracted)) {
                    if (itemInSlot.getCount() + extracted.getCount() <= itemInSlot.getMaxStackSize()) {
                        canPlace = true;
                        placeStack = itemInSlot;
                        placeStack.setCount(itemInSlot.getCount() + extracted.getCount());
                    }
                }
                if (canPlace) {
                    inventory.setItem(toSlot.get() - 1, placeStack);
                } else {
                    return MethodResult.of(false, "Couldn't move items to slot " + toSlot.get());
                }
            } else
                inventory.add(extracted);
        } else {
            return MethodResult.of(false, "Player inventory is full");
        }
        return MethodResult.of(count);
    }

    @LuaFunction(mainThread = true)
    public MethodResult consume(int slot) throws LuaException {
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var player = getPlayer();
        var inventory = getInventory();
        if (player == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        assertBetween(slot, 1, inventory.getContainerSize(), "From slot out of range (%s)");
        if(inventory.getItem(slot-1).getItem().isEdible()){
            player.eat(player.level(),inventory.getItem(slot-1));
            return MethodResult.of(true);
        }
        return MethodResult.of(false, "Item isn't edible");
    }

    @LuaFunction
    public MethodResult lookAt(double x, double y, double z){
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var player = getPlayer();
        if (player == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        player.lookAt(EntityAnchorArgument.Anchor.EYES,new Vec3(x,y,z));
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult displayMessage(String msg, Optional<Boolean> tooltipFlag){
        if (!hasCard()) {
            return MethodResult.of(false, "No card present");
        }
        if (!cardIsBound()) {
            return MethodResult.of(false, "Card isn't bound");
        }
        var player = getPlayer();
        if (player == null) {
            return MethodResult.of(false, "Player isn't present");
        }
        player.displayClientMessage(Component.literal(msg), tooltipFlag.orElse(false));
        return MethodResult.of(true);
    }
    public boolean canAddItem(Inventory inventory,ItemStack p_19184_) {
        boolean flag = false;
        for(ItemStack itemstack : inventory.items) {
            if (itemstack.isEmpty() || ItemStack.isSameItemSameTags(itemstack, p_19184_) && itemstack.getCount() < itemstack.getMaxStackSize()) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    @javax.annotation.Nullable
    private IItemHandler extractHandler(@javax.annotation.Nullable Object object) {
        if (object instanceof BlockEntity blockEntity && blockEntity.isRemoved()) return null;

        if (object instanceof ICapabilityProvider provider) {
            var cap = provider.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (cap.isPresent()) return cap.orElseThrow(NullPointerException::new);
        }

        if (object instanceof IItemHandler handler) return handler;
        if (object instanceof Container container) return new InvWrapper(container);
        return null;
    }
    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        if (world.getBlockState(pos).getBlock() instanceof PlayerInterfaceBlock) {
            var playerInt = new PlayerInterfacePeripheral();
            playerInt.entity = (PlayerInterfaceEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> playerInt);
        }
        return LazyOptional.empty();
    }
}
