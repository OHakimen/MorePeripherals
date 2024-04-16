package com.hakimen.peripherals.client.ber;

import com.hakimen.peripherals.blocks.DiskRaidBlock;
import com.hakimen.peripherals.blocks.tile_entities.DiskRaidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class DiskRaidRenderer implements BlockEntityRenderer<DiskRaidEntity> {

    BlockEntityRendererProvider.Context context;
    public DiskRaidRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }


    @Override
    public void render(DiskRaidEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        var slots = new ItemStack[]{
                entity.inventory.getStackInSlot(0),
                entity.inventory.getStackInSlot(1),
                entity.inventory.getStackInSlot(2),
                entity.inventory.getStackInSlot(3),
                entity.inventory.getStackInSlot(4)
        };

        float x = 0f;
        float z = 0f;
        float rot = 0f;
        for (int i = 0; i < 5; i++) {
            switch (entity.getBlockState().getValue(DiskRaidBlock.FACING)){
                case EAST -> {
                    x += 0.9f;
                    z += 0.5f;
                    rot = 2;
                }
                case WEST -> {
                    x += 0.1f;
                    z += 0.5f;
                    rot = -2f;
                }
                case NORTH -> {
                    z += 0.1f;
                    x += 0.5f;
                    rot = 0.5f;
                }
                case SOUTH -> {
                    z += 0.9f;
                    x += 0.5f;
                    rot = 1;
                }
            }
            stack.pushPose();
            stack.translate(x, 0.225f+((i/10f)*1.25f),z);
            stack.mulPose(new Quaternionf().rotationXYZ(3.1415f/2,0,3.1415f/rot));
            stack.scale(0.5f,0.5f,0.5f);

            itemRenderer.renderStatic(null,
                    slots[i],
                    ItemDisplayContext.FIXED,
                    false,
                    stack,
                    buffer,
                    Minecraft.getInstance().level,
                    packedLight,
                    overlay,
                    0);
            stack.popPose();
            z = 0;
            x = 0;
        }
    }

}
