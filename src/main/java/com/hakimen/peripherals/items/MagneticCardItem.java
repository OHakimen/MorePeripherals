package com.hakimen.peripherals.items;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagneticCardItem extends Item {
    public MagneticCardItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        var data =  (stack.getOrCreateTag().get("data") != null ? stack.getTag().getString("data") : "None");
        var isSensible = (stack.getOrCreateTag().get("sensible") != null ? stack.getTag().getBoolean("sensible") : false);
        if(!isSensible){
            components.add(new TextComponent("Data : ").append(data));
        }else{
            components.add(new TextComponent("Sensible Data").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xaaaaaa))));
        }
        super.appendHoverText(stack, level, components, flag);
    }

}
