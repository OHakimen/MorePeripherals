package com.hakimen.peripherals.screen;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.containers.AdvancedDiskRaidContainer;
import com.hakimen.peripherals.containers.DiskRaidContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AdvancedDiskRaidScreen extends AbstractContainerScreen<AdvancedDiskRaidContainer> {

    private final ResourceLocation GUI = new ResourceLocation(MorePeripherals.mod_id, "textures/gui/advanced_disk_raid_gui.png");

    public AdvancedDiskRaidScreen(AdvancedDiskRaidContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
