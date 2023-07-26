package com.hakimen.peripherals.items;

import com.hakimen.peripherals.blocks.tile_entities.FacadedBlockEntity;
import com.hakimen.peripherals.utils.NBTUtils;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FacadeToolItem extends Item {
    public FacadeToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var block = level.getBlockState(pos);
        if(level.getBlockEntity(pos) instanceof FacadedBlockEntity facaded) {
            if(!level.isClientSide) {
                facaded.setFacade(context.isSecondaryUseActive() ? Blocks.AIR.defaultBlockState() : getFacade(context.getItemInHand()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else if(Block.isShapeFullBlock(block.getShape(level, pos)) && context.isSecondaryUseActive()) {
            context.getItemInHand().getOrCreateTag().put("block", NbtUtils.writeBlockState(block));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        var data =  (stack.getOrCreateTag().get("block") != null ? Component.literal(NBTUtils.readBlockState(stack.getTag().getCompound("block")).toString().replaceAll("Block\\{","").replaceAll("\\}"," ")): Component.translatable("item.peripherals.desc.empty"));
        components.add(data.setStyle(Style.EMPTY.withColor(0x838383)));
        if(data.getString().equals(Component.translatable("item.peripherals.desc.empty").getString())){
            components.add(Component.translatable("item.peripherals.facade_tool.desc").setStyle(Style.EMPTY.withColor(0xa3a3a3)));
        }else{
            components.add(Component.translatable("item.peripherals.facade_tool.desc_has_block").setStyle(Style.EMPTY.withColor(0xa3a3a3)));
        }
        super.appendHoverText(stack, level, components, flag);
    }

    private static BlockState getFacade(ItemStack stack) {
        var tag = stack.getTag();
        return tag != null && tag.contains("block", Tag.TAG_COMPOUND)
            ? NBTUtils.readBlockState(tag.getCompound("block"))
            : Blocks.AIR.defaultBlockState();
    }

}
