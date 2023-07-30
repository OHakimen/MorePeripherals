package com.hakimen.peripherals.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderBagItem extends Item {

    public EnderBagItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        if (!player.isCrouching()) {
            player.openMenu(new SimpleMenuProvider((id, inv, plyr) -> ChestMenu.threeRows(id, inv, plyr.getEnderChestInventory()), Component.translatable("item.peripherals.ender_bag")));
            return InteractionResultHolder.success(stackInHand);
        }
        return InteractionResultHolder.fail(stackInHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("item.peripherals.ender_bag.desc").setStyle(Style.EMPTY.withColor(0x838383)));
        super.appendHoverText(stack, level, components, flag);
    }
}
