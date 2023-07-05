package com.hakimen.peripherals.items;

import com.hakimen.peripherals.utils.NBTUtils;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
                tag.put("facade",context.getItemInHand().getOrCreateTag().getCompound("block"));
            }else if(tag.contains("facade")){
                tag.remove("facade");
            }
            blockEntity.load(tag);
            blockEntity.setChanged();
            return InteractionResult.SUCCESS;
        } else if(!block.is(ModRegistry.Blocks.CABLE.get()) && block.getShape(context.getLevel(),pos) == Block.box(0,0,0,16,16,16) && context.getPlayer().isCrouching()) {
            context.getItemInHand().getOrCreateTag().put("block", NbtUtils.writeBlockState(block));
            return InteractionResult.SUCCESS;
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

}