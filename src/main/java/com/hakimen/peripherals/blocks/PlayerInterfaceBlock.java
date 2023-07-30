package com.hakimen.peripherals.blocks;

import com.hakimen.peripherals.blocks.tile_entities.PlayerInterfaceEntity;
import com.hakimen.peripherals.client.containers.PlayerInterfaceContainer;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class PlayerInterfaceBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public PlayerInterfaceBlock() {
        super(Properties.copy(Blocks.DIRT).strength(2f,2f).sound(SoundType.STONE));
        registerDefaultState( getStateDefinition().any()
                .setValue( FACING, Direction.NORTH ));
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> properties )
    {
        properties.add( FACING );
    }

    @javax.annotation.Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext placement )
    {
        return defaultBlockState().setValue( FACING, placement.getHorizontalDirection().getOpposite() );
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlayerInterfaceEntity(pos,state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof PlayerInterfaceEntity) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("gui.peripherals.player_interface.name");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new PlayerInterfaceContainer(windowId, pos, playerInventory, playerEntity);
                    }
                };
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        var blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof PlayerInterfaceEntity entity){
            level.addFreshEntity(new ItemEntity(level,pos.getX(),pos.getY(),pos.getZ(),entity.inventory.getStackInSlot(0)));
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
