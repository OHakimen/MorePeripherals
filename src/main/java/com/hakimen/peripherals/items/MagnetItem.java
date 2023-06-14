package com.hakimen.peripherals.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagnetItem extends Item {

    public static final String ACTIVE = "active";
    public static final float powerScale = 0.075f;
    public MagnetItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if(stack.getOrCreateTag().contains(ACTIVE)){
            stack.getOrCreateTag().putBoolean(ACTIVE,!stack.getOrCreateTag().getBoolean(ACTIVE));
            return InteractionResultHolder.success(stack);
        }else{
            stack.getOrCreateTag().putBoolean(ACTIVE,true);
            return InteractionResultHolder.success(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if(stack.getOrCreateTag().contains(ACTIVE) && stack.getOrCreateTag().getBoolean(ACTIVE)) {
            components.add(Component.literal("Active").setStyle(Style.EMPTY.withColor(0x838383)));
        }
        super.appendHoverText(stack, level, components, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if(stack.getOrCreateTag().contains(ACTIVE)) {
            return stack.getOrCreateTag().getBoolean(ACTIVE);
        }
        return false;
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if(stack.getOrCreateTag().contains(ACTIVE)){
            if(stack.getOrCreateTag().getBoolean(ACTIVE)){
                AABB range = new AABB(new BlockPos(entity.getBlockX(),entity.getBlockY(),entity.getBlockZ())).inflate(4, 2, 4);
                List<Entity> entityList = level.getEntities(null, range);
                for (var e:entityList) {
                    if(e instanceof ItemEntity item){
                        var x = entity.getX() - item.getX();
                        var y = entity.getY() - item.getY();
                        var z = entity.getZ() - item.getZ();
                        item.addDeltaMovement(new Vec3(x,y,z).normalize().multiply(powerScale,powerScale,powerScale));
                    }
                }
            }
        }
        super.inventoryTick(stack, level, entity, p_41407_, p_41408_);
    }
}
