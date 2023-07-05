package com.hakimen.peripherals.client.model;

import com.hakimen.peripherals.blocks.FacadedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps CC's cable model with one which also renders the facade.
 */
public class CableBakedModel extends BakedModelWrapper<BakedModel> {
    private static final Direction[] RENDER_DIRECTIONS = new Direction[]{
        null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST,
    };

    public CableBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType type) {
        var facade = data.get(FacadedBlockEntity.PROPERTY);
        if (facade == null || facade.isAir() || side != null) return super.getQuads(state, side, rand, data, type);

        var model = getBlockModel(facade);
        if (type != null && !model.getRenderTypes(facade, rand, data).contains(type)) {
            return super.getQuads(state, null, rand, data, type);
        }

        // Include the model quads and all facade quads - this avoids them being incorrectly culled.
        List<List<BakedQuad>> quads = new ArrayList<>(7);
        quads.add(super.getQuads(state, null, rand, data, type));
        for (var direction : RENDER_DIRECTIONS) quads.add(model.getQuads(facade, direction, rand, data, type));
        return ConcatenatedListView.of(quads);
    }

    @NotNull
    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        var facade = data.get(FacadedBlockEntity.PROPERTY);
        return facade == null || facade.isAir()
            ? super.getRenderTypes(state, rand, data)
            : ChunkRenderTypeSet.union(super.getRenderTypes(state, rand, data), getBlockModel(facade).getRenderTypes(facade, rand, data));
    }

    private static BakedModel getBlockModel(BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    }
}
