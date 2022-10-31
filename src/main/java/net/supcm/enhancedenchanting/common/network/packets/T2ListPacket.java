package net.supcm.enhancedenchanting.common.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.supcm.enhancedenchanting.client.block.entity.renderer.WordForgeTileRenderer;
import net.supcm.enhancedenchanting.client.block.entity.renderer.WordMachineTileRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class T2ListPacket {
    public final List<String> list;
    public T2ListPacket(List<String> list) { this.list = list; }
    public static T2ListPacket load(PacketBuffer buffer){
        List<String> list = new ArrayList<>();
        int l = buffer.readVarInt();
        for(int i = 0; i < l; i++)
            list.add(buffer.readUtf(7));
        return new T2ListPacket(list);
    }
    public void save(PacketBuffer buffer) {
        buffer.writeVarInt(list.size());
        for(String str : list)
            buffer.writeUtf(str, 7);
    }
    public static void handle(T2ListPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            WordMachineTileRenderer.T2_LIST.addAll(msg.list);
            WordForgeTileRenderer.T2_LIST.addAll(msg.list);
        });
        ctx.get().setPacketHandled(true);
    }
}
