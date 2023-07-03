package com.hakimen.peripherals.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class NBTUtils {
    public static BlockState readBlockState(CompoundTag p_250775_) {
        ResourceLocation resourcelocation = new ResourceLocation(p_250775_.getString("Name"));
        Block block = ForgeRegistries.BLOCKS.getValue(resourcelocation);

        if (!p_250775_.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            BlockState blockstate = block.defaultBlockState();
            if (p_250775_.contains("Properties", 10)) {
                CompoundTag compoundtag = p_250775_.getCompound("Properties");
                StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();

                for (String s : compoundtag.getAllKeys()) {
                    Property<?> property = statedefinition.getProperty(s);
                    if (property != null) {
                        blockstate = setValueHelper(blockstate, property, s, compoundtag, p_250775_);
                    }
                }
                return blockstate;
            }
        }
        return block.defaultBlockState();
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S p_129205_, Property<T> p_129206_, String p_129207_, CompoundTag p_129208_, CompoundTag p_129209_) {
        Optional<T> optional = p_129206_.getValue(p_129208_.getString(p_129207_));
        if (optional.isPresent()) {
            return p_129205_.setValue(p_129206_, optional.get());
        } else {
            return p_129205_;
        }
    }
}
