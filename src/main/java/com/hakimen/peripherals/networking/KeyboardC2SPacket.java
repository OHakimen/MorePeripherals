package com.hakimen.peripherals.networking;

import com.hakimen.peripherals.registry.ItemRegister;
import dan200.computercraft.shared.computer.blocks.ComputerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeyboardC2SPacket {

    final int key, type;
    final String text;
    final boolean held;
    public KeyboardC2SPacket(int key, int type, String text, boolean held) {
        this.key = key;
        this.text = text;
        this.type = type;
        this.held = held;
    }

    public KeyboardC2SPacket(FriendlyByteBuf buff){
        this.key = buff.readInt();
        this.type = buff.readInt();
        this.text = buff.readUtf();
        this.held = buff.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buff){
        buff.writeInt(this.key);
        buff.writeInt(this.type);
        buff.writeUtf(this.text);
        buff.writeBoolean(this.held);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(()->{
            var player = context.getSender();
            var itemInHand = player != null ? (player.getMainHandItem().getItem().equals(ItemRegister.keyboard.get()) ? player.getMainHandItem() : player.getOffhandItem()) : null;
            if(itemInHand != null && itemInHand.getOrCreateTag().contains("Bind", Tag.TAG_COMPOUND)){
                CompoundTag bindTag = itemInHand.getOrCreateTag().getCompound("Bind");
                int x = bindTag.getInt("x");
                int y = bindTag.getInt("y");
                int z = bindTag.getInt("z");

                var computer = (ComputerBlockEntity)context.getSender().level().getBlockEntity(new BlockPos(x,y,z));
                if(computer != null){
                    var serverComputer = computer.getServerComputer();
                    if(serverComputer != null){
                        switch (type){
                            case 0 -> serverComputer.queueEvent("key", new Object[]{
                                    key,held
                            });
                            case 1 -> serverComputer.queueEvent("key_up", new Object[]{
                                    key
                            });
                            case 2 -> serverComputer.queueEvent("char", new Object[]{
                                    Character.toString(key)
                            });
                            case 3 -> serverComputer.queueEvent("paste", new Object[]{
                                    text
                            });
                            case 4 -> serverComputer.queueEvent("terminate");
                            case 5 -> serverComputer.shutdown();
                            case 6 -> serverComputer.reboot();
                        }
                    }
                }
            }
        });
        return true;
    }
}
