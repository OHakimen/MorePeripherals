package com.hakimen.peripherals.client.ber;

import com.hakimen.peripherals.blocks.ScannerBlock;
import com.hakimen.peripherals.blocks.tile_entities.ScannerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;

import java.util.List;


public class ScannerRenderer implements BlockEntityRenderer<ScannerEntity> {

    BlockEntityRendererProvider.Context context;

    public ScannerRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }


    @Override
    public void render(ScannerEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        stack.pushPose();

        stack.translate(0.5,1.0, 0.5);
        stack.mulPose(new Quaternionf().rotationXYZ(3.1415f/2,0, -(float)((3.14159/2f) * (List.of(Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST).indexOf(entity.getBlockState().getValue(ScannerBlock.FACING))))));
        stack.scale(0.65f,0.65f,0.65f);
        itemRenderer.renderStatic(null,
                entity.inventory.getStackInSlot(0),
                ItemDisplayContext.FIXED,
                false,
                stack,
                buffer,
                Minecraft.getInstance().level,
                packedLight,
                overlay,
                0);
        stack.popPose();

        stack.pushPose();
        stack.translate(0.5,1.03, 0.5);
        stack.mulPose(new Quaternionf().rotationXYZ(3.1415f/2,0, 0));
        itemRenderer.renderStatic(null,
                Items.GLASS_PANE.getDefaultInstance(),
                ItemDisplayContext.FIXED,
                false,
                stack,
                buffer,
                Minecraft.getInstance().level,
                packedLight,
                overlay,
                0);
        stack.popPose();
    }
}
