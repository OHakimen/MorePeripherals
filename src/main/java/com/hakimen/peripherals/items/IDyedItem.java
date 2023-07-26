package com.hakimen.peripherals.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IDyedItem extends DyeableLeatherItem {
    String TAG_COLOR = "color";
    String TAG_DISPLAY = "display";
    int DEFAULT_COLOR = 0xffffff;

    default boolean hasCustomColor(ItemStack p_41114_) {
        CompoundTag compoundtag = p_41114_.getTagElement(TAG_DISPLAY);
        return compoundtag != null && compoundtag.contains(TAG_COLOR, 99);
    }

    default int getColor(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement(TAG_DISPLAY);
        return compoundtag != null && compoundtag.contains(TAG_COLOR, 99) ? compoundtag.getInt(TAG_COLOR) : DEFAULT_COLOR;
    }

    default void clearColor(ItemStack p_41124_) {
        CompoundTag compoundtag = p_41124_.getTagElement(TAG_DISPLAY);
        if (compoundtag != null && compoundtag.contains(TAG_COLOR)) {
            compoundtag.remove(TAG_COLOR);
        }

    }

    default void setColor(ItemStack stack, int color) {
        stack.getOrCreateTagElement(TAG_DISPLAY).putInt(TAG_COLOR, color);
    }

    static ItemStack dyeArmor(ItemStack stack, List<DyeItem> dyes) {
        ItemStack itemstack = ItemStack.EMPTY;
        int[] rgb = new int[3];
        int i = 0;
        int j = 0;
        Item item = stack.getItem();
        IDyedItem iDyedItem = null;
        if (item instanceof IDyedItem) {
            iDyedItem = (IDyedItem) item;
            itemstack = stack.copyWithCount(1);
            if (iDyedItem.hasCustomColor(stack)) {
                int k = iDyedItem.getColor(itemstack);
                float f = (float)(k >> 16 & 255) / 255.0F;
                float f1 = (float)(k >> 8 & 255) / 255.0F;
                float f2 = (float)(k & 255) / 255.0F;
                i += (int)(Math.max(f, Math.max(f1, f2)) * 255.0F);
                rgb[0] += (int)(f * 255.0F);
                rgb[1] += (int)(f1 * 255.0F);
                rgb[2] += (int)(f2 * 255.0F);
                ++j;
            }

            for(DyeItem dyeitem : dyes) {
                float[] afloat = dyeitem.getDyeColor().getTextureDiffuseColors();
                int r = (int)(afloat[0] * 255.0F);
                int g = (int)(afloat[1] * 255.0F);
                int b = (int)(afloat[2] * 255.0F);
                i += Math.max(r, Math.max(g, b));
                rgb[0] += r;
                rgb[1] += g;
                rgb[2] += b;
                ++j;
            }
        }

        if (iDyedItem == null) {
            return ItemStack.EMPTY;
        } else {
            int r = rgb[0] / j;
            int g = rgb[1] / j;
            int b = rgb[2] / j;
            float f3 = (float)i / (float)j;
            float f4 = (float)Math.max(r, Math.max(g, b));
            r = (int)((float)r * f3 / f4);
            g = (int)((float)g * f3 / f4);
            b = (int)((float)b * f3 / f4);
            int finalRGB = (r << 8) + g;
            finalRGB = (finalRGB << 8) + b;
            iDyedItem.setColor(itemstack, finalRGB);
            return itemstack;
        }
    }
}
