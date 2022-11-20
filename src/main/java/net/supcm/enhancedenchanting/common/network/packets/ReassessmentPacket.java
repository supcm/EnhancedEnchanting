package net.supcm.enhancedenchanting.common.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.supcm.enhancedenchanting.client.block.entity.renderer.ReassessmentPillarTileRenderer;
import net.supcm.enhancedenchanting.client.block.entity.renderer.ReassessmentTableTileRenderer;

import java.util.Arrays;
import java.util.function.Supplier;

public class ReassessmentPacket {
    int[] conceptions;
    public ReassessmentPacket(int[] conceptions) { this.conceptions = conceptions;  }
    public static ReassessmentPacket load(PacketBuffer buffer){
        return new ReassessmentPacket(buffer.readVarIntArray());
    }
    public void save(PacketBuffer buffer) {
        buffer.writeVarIntArray(conceptions);
    }
    public static void handle(ReassessmentPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ReassessmentPillarTileRenderer.conceptions = msg.conceptions;
            ReassessmentTableTileRenderer.conceptions = msg.conceptions;
        });
        ctx.get().setPacketHandled(true);
    }
}
