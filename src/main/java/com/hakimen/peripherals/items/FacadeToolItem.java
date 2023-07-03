package com.hakimen.peripherals.items;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FacadeToolItem extends Item {
    public FacadeToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var pos = context.getClickedPos();
        var block = context.getLevel().getBlockState(pos);
        if(block.is(ModRegistry.Blocks.CABLE.get())){
            var blockEntity = (CableBlockEntity) context.getLevel().getBlockEntity(pos);
            CompoundTag tag = blockEntity.saveWithFullMetadata();
            if(context.getItemInHand().getOrCreateTag().contains("block") && !context.getPlayer().isCrouching()){
                tag.putString("facade",context.getItemInHand().getOrCreateTag().getString("block"));
            }else if(tag.contains("facade")){
                tag.remove("facade");
            }
            blockEntity.load(tag);
            blockEntity.setChanged();
            return InteractionResult.SUCCESS;
        } else if(!block.is(ModRegistry.Blocks.CABLE.get()) && block.getShape(context.getLevel(),pos) == Block.box(0,0,0,16,16,16) && context.getPlayer().isCrouching()) {
            context.getItemInHand().getOrCreateTag().putString("block", ForgeRegistries.BLOCKS.getKey(block.getBlock()).toString());
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        var data =  (stack.getOrCreateTag().get("block") != null ? stack.getTag().getString("block") : "Empty");
        components.add(Component.literal(data).setStyle(Style.EMPTY.withColor(0x838383)));
        super.appendHoverText(stack, level, components, flag);
    }

}
