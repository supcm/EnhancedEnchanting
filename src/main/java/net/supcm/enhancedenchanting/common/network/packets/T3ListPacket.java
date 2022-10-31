package net.supcm.enhancedenchanting.common.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.supcm.enhancedenchanting.client.block.entity.renderer.WordForgeTileRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class T3ListPacket {
    public final List<String> list;
    public T3ListPacket(List<String> list) { this.list = list; }
    public static T3ListPacket load(PacketBuffer buffer){
        List<String> list = new ArrayList<>();
        int l = buffer.readVarInt();
        for(int i = 0; i < l; i++)
            list.add(buffer.readUtf(11));
        return new T3ListPacket(list);
    }
    public void save(PacketBuffer buffer) {
        buffer.writeVarInt(list.size());
        for(String str : list)
            buffer.writeUtf(str, 11);
    }
    public static void handle(T3ListPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> WordForgeTileRenderer.T3_LIST.addAll(msg.list));
        ctx.get().setPacketHandled(true);
    }
}
