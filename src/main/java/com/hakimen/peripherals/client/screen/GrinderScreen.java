package com.hakimen.peripherals.client.screen;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.client.containers.GrinderContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GrinderScreen extends AbstractContainerScreen<GrinderContainer> {

    private final ResourceLocation GUI = new ResourceLocation(MorePeripherals.mod_id, "textures/gui/grinder_gui.png");

    public GrinderScreen(GrinderContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        gfx.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
