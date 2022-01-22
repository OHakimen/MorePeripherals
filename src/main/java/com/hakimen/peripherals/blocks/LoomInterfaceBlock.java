package com.hakimen.peripherals.blocks;

import com.hakimen.peripherals.blocks.tile_entities.LoomInterfaceEntity;
import com.hakimen.peripherals.blocks.tile_entities.XPCollectorEntity;
import com.hakimen.peripherals.registry.BlockEntityRegister;
import com.hakimen.peripherals.utils.PlayerStatUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LoomInterfaceBlock extends Block implements EntityBlock {

    public LoomInterfaceBlock() {
        super(Properties.of(Material.STONE).strength(2f,2f).sound(SoundType.STONE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LoomInterfaceEntity(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> p_153214_) {
        return level.isClientSide ? null : ($0,$1,$2,blockEntity) -> {((LoomInterfaceEntity) blockEntity).tick();};
    }

}
