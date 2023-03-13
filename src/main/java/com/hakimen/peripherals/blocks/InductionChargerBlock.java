package com.hakimen.peripherals.blocks;

import com.hakimen.peripherals.blocks.tile_entities.InductionChargerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class InductionChargerBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public InductionChargerBlock() {
        super(Properties.of(Material.STONE).strength(2f,2f).sound(SoundType.STONE));
        registerDefaultState( getStateDefinition().any()
                .setValue( FACING, Direction.NORTH ));
    }
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> properties )
    {
        properties.add( FACING );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos blockPos, CollisionContext collisionContext) {
        var collision = Block.box(0,0,0,16,2,16);
        collision = Shapes.join(collision,Block.box(0,14,0,16,16,16), BooleanOp.OR);
        var direction = state.getValue(FACING);
        switch (direction){
            case NORTH -> {
                collision = Shapes.join(collision,Block.box(0,0,14,16,16,16), BooleanOp.OR);
            }
            case SOUTH -> {
                collision = Shapes.join(collision,Block.box(0,0,0,16,16,2), BooleanOp.OR);
            }
            case WEST -> {
                collision = Shapes.join(collision,Block.box(14,0,0,16,16,16), BooleanOp.OR);
            }
            case EAST -> {
                collision = Shapes.join(collision,Block.box(0,0,0,2,16,16), BooleanOp.OR);
            }
        }
        return collision;
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState mirror( BlockState state, Mirror mirrorIn )
    {
        return state.rotate( mirrorIn.getRotation( state.getValue( FACING ) ) );
    }
    @Nonnull
    @Override
    @Deprecated
    public BlockState rotate( BlockState state, Rotation rot )
    {
        return state.setValue( FACING, rot.rotate( state.getValue( FACING ) ) );
    }

    @javax.annotation.Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext placement )
    {
        return defaultBlockState().setValue( FACING, placement.getHorizontalDirection().getOpposite() );
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InductionChargerEntity(pos,state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> p_153214_) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof InductionChargerEntity tile) {
                tile.tick();
            }
        };}

}
