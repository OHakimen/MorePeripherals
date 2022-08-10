package com.hakimen.peripherals.ber;

import com.hakimen.peripherals.blocks.DiskRaidBlock;
import com.hakimen.peripherals.blocks.tile_entities.DiskRaidEntity;
import com.hakimen.peripherals.blocks.tile_entities.MagneticCardManiputalorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public class MagneticCardManipulatorRenderer implements BlockEntityRenderer<MagneticCardManiputalorEntity> {

    BlockEntityRendererProvider.Context context;

    public MagneticCardManipulatorRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }


    @Override
    public void render(MagneticCardManiputalorEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int overlay, int packedLight) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        stack.pushPose();
        stack.translate(0.5f, 0.525f, 0.5f);
        stack.mulPose(Quaternion.fromXYZ(-3.1415f / 2, -3.1415f/2, -3.1415f /4 ));
        stack.scale(0.75f, 0.75f, 0.75f);
        itemRenderer.renderStatic(null,
                entity.inventory.getStackInSlot(0),
                ItemTransforms.TransformType.FIXED,
                false,
                stack,
                buffer,
                Minecraft.getInstance().level,
                overlay,
                packedLight,
                1);
        stack.popPose();
    }
}
