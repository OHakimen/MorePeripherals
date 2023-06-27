package com.hakimen.peripherals.peripherals;

import com.hakimen.peripherals.blocks.tile_entities.SpawnerInterfaceEntity;
import com.hakimen.peripherals.items.MobDataCardItem;
import com.hakimen.peripherals.registry.BlockRegister;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SpawnerPeripheral implements IPeripheral, IPeripheralProvider {

    SpawnerInterfaceEntity tileEntity;

    @NotNull
    @Override
    public String getType() {
        return "spawner_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult changeSpawner(IComputerAccess computer, String inv, int slot, Optional<Boolean> force) {
        if(hasMultipleSpawners()){
            return MethodResult.of(false, "more than one spawner present");
        }
        if(!hasSpawner()){
            return MethodResult.of(false, "no spawner present");
        }

        slot = slot - 1;
        IPeripheral peripheral = computer.getAvailablePeripheral(inv);
        if (inv == null)
            return MethodResult.of(false, "the input " + inv + " was not found");
        IItemHandler handler = extractHandler(peripheral.getTarget());

        if (slot < 0 || slot > handler.getSlots())
            return MethodResult.of(false, "slot out of range");
        var stack = handler.getStackInSlot(slot);
        if (stack.hasTag() && stack.getItem() instanceof MobDataCardItem) {
            var previousMob = tileEntity.entity.saveWithFullMetadata().getCompound("SpawnData").getCompound("entity").getString("id");
            tileEntity.entity.getSpawner().setEntityId(EntityType.byString(stack.getTag().getString("mob")).get(), tileEntity.getLevel(), tileEntity.getLevel().getRandom(), tileEntity.getBlockPos());
            if (force.isPresent() && force.get()) {
                stack.getTag().remove("mob");
            } else {
                stack.getTag().putString("mob", previousMob);
            }
            stack.resetHoverName();
            CompoundTag tag = new CompoundTag();
            var saved = tileEntity.entity.getSpawner().save(tag);
            tileEntity.entity.getSpawner().load(tileEntity.getLevel(), tileEntity.getBlockPos(), saved);
            tileEntity.setChanged();
            tileEntity.entity.getSpawner().getSpawnerEntity();
            tileEntity.getLevel().sendBlockUpdated(tileEntity.entity.getBlockPos(),tileEntity.spawner,tileEntity.spawner, 3);
            return MethodResult.of(true);
        }
        return MethodResult.of(false);
    }


    @LuaFunction(mainThread = true)
    public final MethodResult getCurrentlySpawningMob() {
        if(hasMultipleSpawners()){
            return MethodResult.of(false, "more than one spawner present");
        }
        if(!hasSpawner()){
            return MethodResult.of(false, "no spawner present");
        }
        CompoundTag tag = new CompoundTag();
        tag = tileEntity.entity.getSpawner().save(tag);
        return MethodResult.of(tag.getCompound("SpawnData").getCompound("entity").getString("id"));
    }

    long lastTime;

    @LuaFunction(mainThread = true)
    public final MethodResult captureSpawner(IComputerAccess computer, Optional<String> inv, Optional<Integer> slot) {
        if(hasMultipleSpawners()){
            return MethodResult.of(false, "more than one spawner present");
        }
        if(!hasSpawner()){
            return MethodResult.of(false, "no spawner present");
        }

        ItemStack spawnerBlock = new ItemStack(Items.SPAWNER);

        CompoundTag tag = new CompoundTag();
        var saved = tileEntity.entity.getSpawner().save(tag);
        if (lastTime + 50 >= System.currentTimeMillis())
            return MethodResult.of(false);
        if (inv.isPresent()) {
            IPeripheral peripheral = computer.getAvailablePeripheral(inv.get());
            if (inv.get() == null)
                return MethodResult.of(false, "the input " + inv.get() + " was not found");
            IItemHandler handler = extractHandler(peripheral.getTarget());
            if (slot.isPresent()) {
                slot = Optional.of(slot.get() - 1);
                if (slot.get() < 0 || slot.get() > handler.getSlots())
                    return MethodResult.of(false, "slot out of range");
                var stack = handler.getStackInSlot(slot.get());
                if (stack.getItem() instanceof MobDataCardItem) {
                    stack.getOrCreateTag().putString("mob", saved.getCompound("SpawnData").getCompound("entity").getString("id"));
                    stack.setHoverName(Component.translatable("item.peripherals.spawner_card").append(" (" + stack.getOrCreateTag().getString("mob") + ")"));
                    var blockPos = tileEntity.entity.getBlockPos();
                    tileEntity.getLevel().addFreshEntity(new ItemEntity(tileEntity.getLevel(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), spawnerBlock));
                    tileEntity.getLevel().destroyBlock(blockPos, false);
                    lastTime = System.currentTimeMillis();
                    return MethodResult.of(true);
                }
            }
        } else if (saved.getCompound("SpawnData").getCompound("entity").getString("id").equals("minecraft:pig")) {
            var blockPos = tileEntity.getBlockPos();
            tileEntity.getLevel().addFreshEntity(new ItemEntity(tileEntity.getLevel(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), spawnerBlock));
            tileEntity.getLevel().destroyBlock(blockPos, false);
            lastTime = System.currentTimeMillis();
            return MethodResult.of(true);
        }
        return MethodResult.of(false);
    }

    public boolean hasSpawner(){
        return tileEntity.entity != null;
    }

    public boolean hasMultipleSpawners() {
        return tileEntity.hasMultipleSpawners;
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
        if (world.getBlockState(pos).getBlock().equals(BlockRegister.spawnerInterfaceBlock.get())) {
            this.tileEntity = (SpawnerInterfaceEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> this);
        }
        return LazyOptional.empty();
    }
}
