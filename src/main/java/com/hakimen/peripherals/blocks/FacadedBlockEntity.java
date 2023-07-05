package com.hakimen.peripherals.blocks;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelProperty;

/**
 * A {@link BlockEntity} which can have a facade applied.
 */
public interface FacadedBlockEntity {
    ModelProperty<BlockState> PROPERTY = new ModelProperty<>();

    BlockState getFacade();

    void setFacade(BlockState state);
}
