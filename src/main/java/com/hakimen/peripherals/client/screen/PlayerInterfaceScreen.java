package com.hakimen.peripherals.client.screen;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.client.containers.PlayerInterfaceContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PlayerInterfaceScreen extends AbstractContainerScreen<PlayerInterfaceContainer> {

    private final ResourceLocation GUI = new ResourceLocation(MorePeripherals.mod_id, "textures/gui/ender_chest_interface_gui.png");

    public PlayerInterfaceScreen(PlayerInterfaceContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, partialTicks);
        this.renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        gfx.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
