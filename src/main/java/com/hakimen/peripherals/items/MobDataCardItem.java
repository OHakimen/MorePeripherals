package com.hakimen.peripherals.items;

import com.hakimen.peripherals.config.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class MobDataCardItem extends Item {
    static final String MOB = "mob";
    public MobDataCardItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if(entity instanceof LivingEntity livingEntity && !(entity instanceof Player) ){
            Random r = new Random();
            if (r.nextFloat() < Config.mobDataCaptureChance.get()) {
                stack.setHoverName(null);
                stack.getOrCreateTag().putString(MOB, livingEntity.getEncodeId());
                stack.setHoverName(Component.translatable("item.peripherals.spawner_card").append(" ("+livingEntity.getEncodeId()+")"));
                return false;
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }



    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        stack.getOrCreateTag();
        if (stack.getTag() != null){
            components.add((stack.getTag().get(MOB) != null ?
                    Component.literal(stack.getTag().getString(MOB)).setStyle(Style.EMPTY.withColor(0x838383))
                    : Component.translatable("item.peripherals.desc.empty").setStyle(Style.EMPTY.withColor(0x838383))));
        }
        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}
