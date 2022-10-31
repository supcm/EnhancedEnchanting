package net.supcm.enhancedenchanting.common.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.supcm.enhancedenchanting.client.block.entity.renderer.MatrixTileRenderer;
import java.util.function.Supplier;

public class MatrixDoCraftPacket {
    public final boolean doCraft;
    public final int ticks;
    public MatrixDoCraftPacket(boolean doCraft, int ticks) {
        this.doCraft = doCraft;
        this.ticks = ticks;
    }
    public static MatrixDoCraftPacket load(PacketBuffer buffer){
        return new MatrixDoCraftPacket(buffer.readBoolean(), buffer.readInt());
    }
    public void save(PacketBuffer buffer) {
        buffer.writeBoolean(doCraft);
        buffer.writeInt(ticks);
    }
    public static void handle(MatrixDoCraftPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            MatrixTileRenderer.doCraft = msg.doCraft;
            MatrixTileRenderer.tick = msg.ticks;
        });
        ctx.get().setPacketHandled(true);
    }
}
