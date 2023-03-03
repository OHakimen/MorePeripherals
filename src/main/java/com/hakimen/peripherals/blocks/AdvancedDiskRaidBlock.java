package com.hakimen.peripherals.blocks;

import com.hakimen.peripherals.blocks.tile_entities.AdvancedDiskRaidEntity;
import com.hakimen.peripherals.blocks.tile_entities.DiskRaidEntity;
import com.hakimen.peripherals.containers.AdvancedDiskRaidContainer;
import com.hakimen.peripherals.containers.DiskRaidContainer;
import com.hakimen.peripherals.screen.AdvancedDiskRaidScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class AdvancedDiskRaidBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AdvancedDiskRaidBlock() {
        super(Properties.of(Material.STONE).strength(2f,2f).sound(SoundType.STONE));
        registerDefaultState( getStateDefinition().any()
                .setValue( FACING, Direction.NORTH ));
    }
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> properties )
    {
        properties.add( FACING );
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
        return new AdvancedDiskRaidEntity(pos,state);
    }
    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof AdvancedDiskRaidEntity) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("gui.peripherals.advanceddiskraid.name");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new AdvancedDiskRaidContainer(windowId, pos, playerInventory, playerEntity);
                    }
                };
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        var blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof AdvancedDiskRaidEntity raid){
            for (int i = 0; i < 10; i++) {
                level.addFreshEntity(new ItemEntity(level,pos.getX(),pos.getY(),pos.getZ(),raid.inventory.getStackInSlot(i)));
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> p_153214_) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof AdvancedDiskRaidEntity tile) {
                tile.tick();
            }
        };}

}
