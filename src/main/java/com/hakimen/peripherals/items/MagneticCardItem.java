package com.hakimen.peripherals.items;

import com.hakimen.peripherals.blocks.tile_entities.MagneticCardManiputalorEntity;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
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
            components.add(Component.literal("Data : ").append(data));
        }else{
            components.add(Component.literal("Sensible Data").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xaaaaaa))));
        }
        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof MagneticCardManiputalorEntity manip){
            var data = context.getItemInHand().getOrCreateTag().get("data") != null ? context.getItemInHand().getTag().get("data") : "";
            for (IComputerAccess c:manip.computers) {
                if(data != ""){
                    data = data.toString().substring(1,data.toString().length()-1);
                    c.queueEvent("card_read",data);
                }

            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
