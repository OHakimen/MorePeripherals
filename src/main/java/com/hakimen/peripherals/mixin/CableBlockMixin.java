package com.hakimen.peripherals.mixin;

import com.hakimen.peripherals.blocks.FacadedBlockEntity;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlock;
import dan200.computercraft.shared.peripheral.modem.wired.CableShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CableBlock.class)
abstract class CableBlockMixin extends Block {
    public CableBlockMixin(Properties properties) {
        super(properties);
    }

    /**
     * @author JheffersonMarques
     * @reason Add support for facades.
     */
    @Overwrite
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        var blockEntity = getter.getBlockEntity(pos);
        return blockEntity instanceof FacadedBlockEntity facaded && !facaded.getFacade().isAir()
            ? Shapes.block()
            : CableShapes.getShape(state);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        // Ignore facade when computing occlusion.
        return CableShapes.getCableShape(state);
    }

    @Override
    public boolean hasDynamicShape() {
        // Prevent caching of shapes
        return true;
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        return level.getBlockEntity(pos) instanceof FacadedBlockEntity facaded && !facaded.getFacade().isAir() ? facaded.getFacade() : state;
    }
}
