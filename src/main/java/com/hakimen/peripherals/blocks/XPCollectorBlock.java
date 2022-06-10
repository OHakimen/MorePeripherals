package com.hakimen.peripherals.blocks;

import com.hakimen.peripherals.blocks.tile_entities.GrinderEntity;
import com.hakimen.peripherals.blocks.tile_entities.TradingInterfaceEntity;
import com.hakimen.peripherals.blocks.tile_entities.XPBottlerEntity;
import com.hakimen.peripherals.blocks.tile_entities.XPCollectorEntity;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import com.hakimen.peripherals.registry.BlockRegister;
import com.hakimen.peripherals.utils.PlayerStatUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class XPCollectorBlock extends Block implements EntityBlock {

    public XPCollectorBlock() {
        super(Properties.of(Material.STONE).strength(2f,2f).sound(SoundType.STONE));
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new XPCollectorEntity(pos,state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var blockEntity = level.getBlockEntity(pos);
        if(blockEntity.getType() == BlockEntityRegister.xpCollectorEntity.get()){
            ((XPCollectorEntity) blockEntity).xpPoints+= PlayerStatUtil.getExpAtLevel(player.experienceLevel);
            player.experienceLevel = 0;
            blockEntity.setChanged();
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        var blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof XPCollectorEntity collectorEntity && collectorEntity.xpPoints > 0){
            ItemStack stack = new ItemStack(BlockRegister.xpCollectorItem.get());
            collectorEntity.saveToItem(stack);
            level.addFreshEntity(new ItemEntity(level,pos.getX(),pos.getY(),pos.getZ(),stack));
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> p_153214_) {
        return level.isClientSide ? null : ($0,$1,$2,blockEntity) -> {((XPCollectorEntity) blockEntity).tick();};
    }

}
