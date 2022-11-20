package net.supcm.enhancedenchanting.common.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.network.packets.*;
import java.util.Optional;

public class PacketHandler {
    public static final String PROTOCOL_VERSION = "5.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EnhancedEnchanting.MODID, "network"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    public static void init() {
        int index = -1;
        CHANNEL.registerMessage(index++, T2ListPacket.class, T2ListPacket::save, T2ListPacket::load,
                T2ListPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(index++, T3ListPacket.class, T3ListPacket::save, T3ListPacket::load,
                T3ListPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(index++, MatrixDoCraftPacket.class, MatrixDoCraftPacket::save, MatrixDoCraftPacket::load,
                MatrixDoCraftPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(index++, CodexScreenPacket.class, CodexScreenPacket::save, CodexScreenPacket::load,
                CodexScreenPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(index++, ReassessmentPacket.class, ReassessmentPacket::save, ReassessmentPacket::load,
                ReassessmentPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
