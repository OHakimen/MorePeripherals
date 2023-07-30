package com.hakimen.peripherals.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerCardItem extends Item {

    static final String BIND = "Bind";
    static final String OWNER_NAME = "Owner";
    public PlayerCardItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        if(player.isCrouching()){
            stackInHand.getOrCreateTag().putUUID(BIND,player.getUUID());
            stackInHand.getOrCreateTag().putString(OWNER_NAME, player.getDisplayName().getString());
            player.displayClientMessage(Component.translatable("item.peripherals.player_card.bound", player.getDisplayName().getString()),true);
            return InteractionResultHolder.success(stackInHand);
        }
        return InteractionResultHolder.fail(stackInHand);
    }

    @Override
    public boolean isFoil(ItemStack stack) {

        return stack.getOrCreateTag().contains(OWNER_NAME, Tag.TAG_STRING);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(OWNER_NAME, Tag.TAG_STRING)){
            String owner = tag.getString(OWNER_NAME);
            components.add(Component.translatable("item.peripherals.player_card.bound",owner).setStyle(Style.EMPTY.withColor(0x838383)));
        }
        super.appendHoverText(stack, level, components, flag);
    }
}
