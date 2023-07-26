package com.hakimen.peripherals.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;


public class XPBottlerBlock extends Block{
    public XPBottlerBlock() {
        super(Properties.copy(Blocks.STONE).strength(2f,2f).sound(SoundType.STONE));
    }

}
