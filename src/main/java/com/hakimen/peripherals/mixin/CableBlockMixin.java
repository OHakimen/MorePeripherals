package com.hakimen.peripherals.mixin;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlock;
import dan200.computercraft.shared.peripheral.modem.wired.CableShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CableBlock.class,remap = false)
public class CableBlockMixin extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public CableBlockMixin(Properties p_49795_) {
        super(p_49795_.dynamicShape());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModRegistry.BlockEntities.CABLE.get().create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        var blockEntity = getter.getBlockEntity(pos);
        var data = blockEntity != null ? blockEntity.saveWithFullMetadata() : new CompoundTag();
        return (data.contains("facade") && !data.getString("facade").equals("")) ?
                Block.box(0,0,0,16,16,16) : CableShapes.getShape(state);
    }


    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return super.getRenderShape(p_60550_);
    }
}
