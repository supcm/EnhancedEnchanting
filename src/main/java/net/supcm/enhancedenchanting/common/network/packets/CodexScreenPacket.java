package net.supcm.enhancedenchanting.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.client.gui.CodexScreen;
import java.util.function.Supplier;

public class CodexScreenPacket {
    public final CompoundNBT tag;
    public CodexScreenPacket(CompoundNBT tag) { this.tag = tag;  }
    public static CodexScreenPacket load(PacketBuffer buffer){
        return new CodexScreenPacket(buffer.readNbt());
    }
    public void save(PacketBuffer buffer) {
        buffer.writeNbt(tag);
    }
    public static void handle(CodexScreenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> openScreen(msg));
        ctx.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void openScreen(CodexScreenPacket msg) {
        Minecraft.getInstance().setScreen(new CodexScreen(msg.tag.getList("Revealed", 8)));
    }
}
