package com.hakimen.peripherals.items;

import com.hakimen.peripherals.blocks.tile_entities.MagneticCardManiputalorEntity;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagneticCardItem extends Item implements IDyedItem {
    static final String DATA = "data";
    static final String SENSIBLE = "sensible";
    public MagneticCardItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        var data = stack.getOrCreateTag().get(DATA) != null ? Component.literal(stack.getTag().getString(DATA)) : Component.translatable("item.peripherals.desc.empty");
        var isSensible = (stack.getOrCreateTag().get(SENSIBLE) != null && stack.getTag().getBoolean(SENSIBLE));
        if(!isSensible){
            components.add(data.setStyle(Style.EMPTY.withColor(0x838383)));
        }else{
            components.add(Component.translatable("item.peripherals.magnetic_card.sensible").setStyle(Style.EMPTY.withColor(0x838383)));
        }
        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof MagneticCardManiputalorEntity manip){
            var data = context.getItemInHand().getOrCreateTag().get(DATA) != null ? context.getItemInHand().getTag().get(DATA) : "";
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
