package com.hakimen.peripherals.items;

import com.hakimen.peripherals.client.containers.KeyboardContainer;
import com.hakimen.peripherals.config.Config;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeyboardItem extends Item {

    static final String BIND = "Bind";
    public KeyboardItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var clickPos = context.getClickedPos();
        var stack = context.getItemInHand();
        var player =  context.getPlayer();
        if(level.getBlockEntity(clickPos) instanceof AbstractComputerBlockEntity){
            CompoundTag pos = new CompoundTag();
            pos.putInt("x",clickPos.getX());
            pos.putInt("y",clickPos.getY());
            pos.putInt("z",clickPos.getZ());
            stack.getOrCreateTag().put(BIND,pos);
            if(player != null){
                player.displayClientMessage(Component.translatable("item.peripherals.keyboard.bound",clickPos.getX(),clickPos.getY(),clickPos.getZ()),true);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        int x = itemStack.getOrCreateTag().getCompound(BIND).getInt("x");
        int y = itemStack.getOrCreateTag().getCompound(BIND).getInt("y");
        int z = itemStack.getOrCreateTag().getCompound(BIND).getInt("z");
        if(Math.floor(player.blockPosition().getCenter().distanceTo(new Vec3(x,y,z))) <= Config.keyboardRange.get()){
            if(itemStack.getOrCreateTag().contains(BIND, Tag.TAG_COMPOUND)  && !player.isCrouching()) {
                if (!(level.getBlockEntity(new BlockPos(x,y,z)) instanceof AbstractComputerBlockEntity)){
                    player.displayClientMessage(Component.translatable("item.peripherals.keyboard.not_found"),true);
                }else if (!level.isClientSide) {
                    MenuProvider containerProvider = new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.empty();
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                            return new KeyboardContainer(windowId);
                        }
                    };
                    NetworkHooks.openScreen((ServerPlayer) player, containerProvider);
                }
            } else if(!itemStack.getOrCreateTag().contains(BIND, Tag.TAG_COMPOUND)) {
                player.displayClientMessage(Component.translatable("item.peripherals.keyboard.not_bound"),true);
            }
        }else{
            player.displayClientMessage(Component.translatable("item.peripherals.keyboard.out_of_range"),true);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if(itemStack.getOrCreateTag().contains(BIND, Tag.TAG_COMPOUND)){
            int x = itemStack.getOrCreateTag().getCompound(BIND).getInt("x");
            int y = itemStack.getOrCreateTag().getCompound(BIND).getInt("y");
            int z = itemStack.getOrCreateTag().getCompound(BIND).getInt("z");
            components.add(Component.translatable("item.peripherals.keyboard.bound", x, y, z).setStyle(Style.EMPTY.withColor(0x838383)));
        }else{
            components.add(Component.translatable("item.peripherals.keyboard.unbound").setStyle(Style.EMPTY.withColor(0x838383)));
        }
        super.appendHoverText(itemStack, level, components, tooltipFlag);
    }


}
