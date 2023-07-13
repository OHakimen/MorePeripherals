package com.hakimen.peripherals.client.screen;

import com.hakimen.peripherals.client.containers.KeyboardContainer;
import com.hakimen.peripherals.networking.KeyboardC2SPacket;
import com.hakimen.peripherals.registry.PacketRegister;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.BitSet;

public class KeyboardScreen extends AbstractContainerScreen<KeyboardContainer> {


    @Override
    protected void init() {
        minecraft.mouseHandler.grabMouse();
        minecraft.screen = this;
        KeyMapping.releaseAll();
        super.init();
    }
    public KeyboardScreen(KeyboardContainer keyboardContainer, Inventory inventory, Component title) {
        super(keyboardContainer, inventory, title);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        gfx.drawCenteredString(Minecraft.getInstance().font, Component.translatable("item.peripherals.keyboard.close"),this.width/2,this.height/8,0xffffff);
    }

    @Override
    protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {

    }


    private float terminateTimer = -1;
    private float rebootTimer = -1;
    private float shutdownTimer = -1;

    private static final float TERMINATE_TIME = 0.5f;
    private static final float KEY_SUPPRESS_DELAY = 0.2f;

    private final BitSet keysDown = new BitSet(256);

    @Override
    public boolean charTyped(char ch, int modifiers) {
        if (ch >= 32 && ch <= 126 || ch >= 160 && ch <= 255) {
            PacketRegister.sendToServer(new KeyboardC2SPacket(ch,2,"",false));
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int button) {
        var hit = getMinecraft().hitResult;
        if(hit != null && hit.getType() == HitResult.Type.BLOCK && button == 1){
            getMinecraft().gameMode.useItemOn(getMinecraft().player, InteractionHand.MAIN_HAND, (BlockHitResult) getMinecraft().hitResult);
        }
        return super.mouseClicked(p_97748_, p_97749_, button);
    }

    @Override
    public boolean keyPressed( int key, int scancode, int modifiers) {
        if (key == 256) return super.keyPressed(key,scancode,modifiers);
        if (Screen.isPaste(key)) {
            doPaste();
            return true;
        }

        if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            switch (key) {
                case GLFW.GLFW_KEY_T -> {
                    //Terminate
                    if (terminateTimer < 0) terminateTimer = 0;
                }
                case GLFW.GLFW_KEY_S -> {
                    //Shutdown
                    if (shutdownTimer < 0) shutdownTimer = 0;

                }
                case GLFW.GLFW_KEY_R -> {
                    //Reboot
                    if (rebootTimer < 0) rebootTimer = 0;
                }
            }
        }

        if (key >= 0 && terminateTimer < KEY_SUPPRESS_DELAY && rebootTimer < KEY_SUPPRESS_DELAY && shutdownTimer < KEY_SUPPRESS_DELAY) {
            var repeat = keysDown.get(key);
            keysDown.set(key);
            PacketRegister.sendToServer(new KeyboardC2SPacket(key,0,"",repeat));
        }
        return true;
    }

    @Override
    protected void containerTick() {
        if (terminateTimer >= 0 && terminateTimer < TERMINATE_TIME && (terminateTimer += 0.05f) > TERMINATE_TIME) {
            PacketRegister.sendToServer(new KeyboardC2SPacket(0,4,"",false));
        }

        if (shutdownTimer >= 0 && shutdownTimer < TERMINATE_TIME && (shutdownTimer += 0.05f) > TERMINATE_TIME) {
            PacketRegister.sendToServer(new KeyboardC2SPacket(0,5,"",false));
        }

        if (rebootTimer >= 0 && rebootTimer < TERMINATE_TIME && (rebootTimer += 0.05f) > TERMINATE_TIME) {
            PacketRegister.sendToServer(new KeyboardC2SPacket(0,6,"",false));
        }
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        if (key >= 0 && keysDown.get(key)) {
            keysDown.set(key, false);
            PacketRegister.sendToServer(new KeyboardC2SPacket(key,1,"",false));
        }

        switch (key) {
            case GLFW.GLFW_KEY_T -> terminateTimer = -1;
            case GLFW.GLFW_KEY_R -> rebootTimer = -1;
            case GLFW.GLFW_KEY_S -> shutdownTimer = -1;
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL ->
                    terminateTimer = rebootTimer = shutdownTimer = -1;
        }

        return true;
    }

    private void doPaste() {
        var clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();

        var carriageReturnIndex = clipboard.indexOf('\r');
        var newLineIndex = clipboard.indexOf('\n');
        if (carriageReturnIndex >= 0 && newLineIndex >= 0) {
            clipboard = clipboard.substring(0, Math.min(carriageReturnIndex, newLineIndex));
        } else if (carriageReturnIndex >= 0) {
            clipboard = clipboard.substring(0, newLineIndex);
        } else if (newLineIndex >= 0) {
            clipboard = clipboard.substring(0, newLineIndex);
        }

        clipboard = SharedConstants.filterText(clipboard);
        if (!clipboard.isEmpty()) {
            if (clipboard.length() > 512) clipboard = clipboard.substring(0, 512);
            PacketRegister.sendToServer(new KeyboardC2SPacket(0,3,clipboard,false));
        }
    }
}
