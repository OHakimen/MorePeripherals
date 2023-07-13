package com.hakimen.peripherals.registry;

import com.hakimen.peripherals.MorePeripherals;
import com.hakimen.peripherals.networking.KeyboardC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketRegister {
    private static SimpleChannel INSTANCE;

    private static int idPacket = 0;

    private static int id(){
        return idPacket++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MorePeripherals.mod_id, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(KeyboardC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(KeyboardC2SPacket::new)
                .encoder(KeyboardC2SPacket::toBytes)
                .consumerMainThread(KeyboardC2SPacket::handle)
                .add();

    }

    public static <MSG> void sendToServer(MSG msg){
        INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sendToClient(MSG msg, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(()-> player), msg);
    }

    public static <MSG> void sendToClients(MSG msg){
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
