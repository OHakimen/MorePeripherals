package com.hakimen.peripherals.client.ber;

import com.hakimen.peripherals.blocks.MagneticCardManipulatorBlock;
import com.hakimen.peripherals.blocks.tile_entities.MagneticCardManiputalorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;


public class MagneticCardManipulatorRenderer implements BlockEntityRenderer<MagneticCardManiputalorEntity> {

    BlockEntityRendererProvider.Context context;

    public MagneticCardManipulatorRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }


    @Override
    public void render(MagneticCardManiputalorEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        stack.pushPose();
        switch(entity.getBlockState().getValue(MagneticCardManipulatorBlock.FACING)){
            case NORTH -> {
                stack.translate(0.5f, 0.5f, 0.05f);
                stack.mulPose(new Quaternionf().rotationXYZ(-3.1415f , -3.1415f/2, -3.1415f/4 ));
            }
            case SOUTH -> {
                stack.translate(0.5f, 0.5f, 0.95f);
                stack.mulPose(new Quaternionf().rotationXYZ(3.1415f/2 + -3.1415f/4 , 3.1415f/2, 3.1415f/2  ));
            }
            case WEST -> {
                stack.translate(0.05f, 0.5f, 0.5f);
                stack.mulPose(new Quaternionf().rotationXYZ(3.1415f, 3.1415f, 3.1415f/4 -3.1415f/2 ));
            }
            case EAST -> {
                stack.translate(0.95f, 0.5f, 0.5f);
                stack.mulPose(new Quaternionf().rotationXYZ(3.1415f, 3.1415f, 3.1415f/4  + 3.1415f/2 ));
            }
            case DOWN -> {
                stack.translate(0.5f, 0.05f, 0.5f);
                stack.mulPose(new Quaternionf().rotationXYZ(3.1415f/4  + 3.1415f/2 , 3.1415f/2, 3.1415f/2  ));
            }
            case UP -> {
                stack.translate(0.5f, 0.95f, 0.5f);
                stack.mulPose(new Quaternionf().rotationXYZ(3.1415f/4  - 3.1415f/2 , 3.1415f/2, 3.1415f/2  ));
            }
        }

        stack.scale(0.75f, 0.75f, 0.75f);
        itemRenderer.renderStatic(null,
                entity.inventory.getStackInSlot(0),
                ItemDisplayContext.FIXED,
                false,
                stack,
                buffer,
                Minecraft.getInstance().level,
                packedLight,
                overlay,
                1);
        stack.popPose();
    }
}
