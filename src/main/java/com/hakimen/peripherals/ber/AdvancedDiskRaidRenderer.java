package com.hakimen.peripherals.ber;

import com.hakimen.peripherals.blocks.DiskRaidBlock;
import com.hakimen.peripherals.blocks.tile_entities.AdvancedDiskRaidEntity;
import com.hakimen.peripherals.blocks.tile_entities.DiskRaidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public class AdvancedDiskRaidRenderer implements BlockEntityRenderer<AdvancedDiskRaidEntity> {

    BlockEntityRendererProvider.Context context;
    public AdvancedDiskRaidRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }


    @Override
    public void render(AdvancedDiskRaidEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int overlay, int packedLight) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        var slots = new ItemStack[][]{
                {
                    entity.inventory.getStackInSlot(0),
                    entity.inventory.getStackInSlot(1),
                    entity.inventory.getStackInSlot(2),
                    entity.inventory.getStackInSlot(3),
                    entity.inventory.getStackInSlot(4)
                },
                {
                        entity.inventory.getStackInSlot(5),
                        entity.inventory.getStackInSlot(6),
                        entity.inventory.getStackInSlot(7),
                        entity.inventory.getStackInSlot(8),
                        entity.inventory.getStackInSlot(9)
                }};

        float x = 0f;
        float z = 0f;
        float rot = 0f;
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 5; i++) {
                switch (entity.getBlockState().getValue(DiskRaidBlock.FACING)){
                    case EAST -> {
                        x += 0.9f;
                        z += 0.25f + j/2f;
                        rot = 2;
                    }
                    case WEST -> {
                        x += 0.1f;
                        z += 0.25f + j/2f;
                        rot = -2f;
                    }
                    case NORTH -> {
                        z += 0.1f;
                        x += 0.25f + j/2f;
                        rot = 0.5f;
                    }
                    case SOUTH -> {
                        z += 0.9f;
                        x += 0.25f + j/2f;
                        rot = 1;
                    }
                }
                stack.pushPose();
                stack.translate(x, 0.225f+((i/10f)*1.25f),z);
                stack.mulPose(Quaternion.fromXYZ(3.1415f/2,0,3.1415f/rot));
                stack.scale(0.4f,0.4f,0.4f);

                itemRenderer.renderStatic(null,
                        slots[j][i],
                        ItemTransforms.TransformType.FIXED,
                        false,
                        stack,
                        buffer,
                        Minecraft.getInstance().level,
                        overlay,
                        packedLight,
                        1);
                stack.popPose();
                z = 0;
                x = 0;
            }
        }
    }

}
